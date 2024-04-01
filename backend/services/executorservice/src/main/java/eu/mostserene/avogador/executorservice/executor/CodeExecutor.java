package eu.mostserene.avogador.executorservice.executor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import eu.mostserene.avogador.executorservice.executor.languages.Language;
import eu.mostserene.avogador.executorservice.projectsubmission.ProjectSubmission;
import eu.mostserene.avogador.executorservice.storage.StorageService;
import eu.mostserene.avogador.executorservice.submission.CodingSubmission;
import eu.mostserene.avogador.executorservice.submission.SubmissionOutput;
import eu.mostserene.avogador.executorservice.submission.SubmissionResult;
import eu.mostserene.avogador.executorservice.submission.SubmissionStatus;
import eu.mostserene.avogador.executorservice.utils.LoggerColors;
import eu.mostserene.avogador.executorservice.utils.LoggerUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.core.env.Environment;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Slf4j
public class CodeExecutor {
    @Getter
    private static CodeExecutor executor;
    private final Set<Language> languages = new HashSet<>();
    private DockerClient dockerClient;
    private StorageService storageService;
    private CommunicationUtils communicationUtils;

    private CodeExecutor() {
    }

    @PostConstruct
    static void configure(Environment environment, StorageService storageService, CommunicationUtils communicationUtils) {
        CodeExecutor.executor = new CodeExecutor();

        DefaultDockerClientConfig config = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .withDockerHost(Objects.requireNonNull(environment.getProperty("executor.docker.host")))
                .withDockerTlsVerify(false)
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofMinutes(10))
                .build();

        executor.dockerClient = DockerClientImpl.getInstance(config, httpClient);

        executor.storageService = storageService;

        executor.communicationUtils = communicationUtils;

        /*
        executor.dockerClient.listContainersCmd().withShowAll(true).exec()
                .stream()
                .peek(container -> log.info(LoggerColors.error(Arrays.toString(container.getNames()))))
                .filter(container -> "gotti27/runtime-env:stable".equals(container.getImage()))
                .peek(container -> log.info(LoggerColors.error(Arrays.toString(container.getNames()))))
                .peek(container -> {
                    log.info(LoggerColors.cyan(container.getStatus()));
                    if ("running".equals(container.getState())) {
                        executor.dockerClient.killContainerCmd(container.getId()).exec();
                    }
                })
                .forEach(container -> executor.dockerClient.removeContainerCmd(container.getId()).exec());
         */

        File executorFolder = new File("/avogador");

        try {
            FileUtils.deleteDirectory(executorFolder);
            log.info(LoggerColors.success("Executor local folder cleaned up"));
        } catch (IOException e) {
            log.error(LoggerColors.error("Executor local folder clean up failed"));
            throw new RuntimeException(e);
        }

        if (executorFolder.mkdirs()) {
            log.info(LoggerColors.success("Executor local folder created"));
        }

        new Reflections(new ConfigurationBuilder()
                .forPackages("eu.mostserene.avogador.executorservice.executor.languages"))
                .getSubTypesOf(Language.class)
                .forEach(executor::loadLanguage);

        try {
            pullImages();
        } catch (InterruptedException e) {
            System.exit(1);
            throw new RuntimeException(e);
        }
    }

    private static void pullImages() throws InterruptedException {
        log.info(LoggerColors.warn("pulling images required by executor"));
        executor.dockerClient.pullImageCmd("gotti27/runtime-env")
                .withTag("stable")
                .exec(new ResultCallback.Adapter<>() {
                    @Override
                    public void onNext(PullResponseItem item) {
                        super.onNext(item);
                    }
                }).awaitCompletion();
        executor.dockerClient.pullImageCmd("ubuntu")
                .withTag("latest")
                .exec(new ResultCallback.Adapter<>() {
                    @Override
                    public void onNext(PullResponseItem item) {
                        super.onNext(item);
                    }
                }).awaitCompletion();
        executor.dockerClient.pullImageCmd("gotti27/j-check-env")
                .withTag("latest")
                .exec(new ResultCallback.Adapter<>() {
                    @Override
                    public void onNext(PullResponseItem item) {
                        super.onNext(item);
                    }
                }).awaitCompletion();
        log.info(LoggerColors.success("> image pulled"));
    }

    private void loadLanguage(Class<? extends Language> languageType) {
        try {
            Language language = languageType.getDeclaredConstructor().newInstance();
            languages.add(language);
            log.info(LoggerColors.success("> " + language.getName() + " added"));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            log.error(LoggerColors.error("> failed to load a language"));
        }
    }

    public void checkSubmission(CodingSubmission codingSubmission) {
        log.info(LoggerColors.warn("Submission " + codingSubmission.getId() + ": Execution started"));

        File submissionFolder = createFolder(codingSubmission);
        String compileOutput = null;
        try {
            File code = storageService.fetchAndSaveSubmissionCode(codingSubmission);
            File testcases = storageService.fetchAndSaveTestcases(codingSubmission);

            Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
            archiver.extract(code, new File(code.getParentFile() + "/code"));
            archiver.extract(testcases, new File(testcases.getParentFile() + "/testcases"));

            Pair<File, String> compiled = compile(new File(code.getParentFile() + "/code/" + codingSubmission.getFilename()));

            File executable = compiled.getLeft();
            compileOutput = compiled.getRight();

            if (isCompilationFailed(executable)) {
                handleCompilationFailure(codingSubmission, compileOutput);
            } else {
                handleCompilationSuccess(executable, codingSubmission, compileOutput);
            }
        } catch (NotFoundException e) {
            handleCompilationFailure(codingSubmission, compileOutput);
        } catch (Exception e) {
            handleGeneralException(codingSubmission, e);
        } finally {
            FileSystemUtils.deleteRecursively(submissionFolder);
            log.info(LoggerColors.success("Submission " + codingSubmission.getId() + ": Cleanup completed"));
        }
    }

    private void handleCompilationSuccess(File executable, CodingSubmission codingSubmission, String compileOutput) throws IOException {
        setupExecutablePermissions(executable);
        log.info(LoggerColors.success("Submission " + codingSubmission.getId() + ": Compiled successfully"));
        communicationUtils.postOutput(new SubmissionOutput(codingSubmission, "compile", compileOutput));

        codingSubmission.getTestcases()
                .forEach(testcase -> testCaseExecutionWrapper(testcase, codingSubmission, executable));

        log.info(LoggerColors.success("Submission " + codingSubmission.getId() + ": Execution done"));
    }

    private void handleGeneralException(CodingSubmission codingSubmission, Exception e) {
        log.info(LoggerColors.error("Submission " + codingSubmission.getId() + ": Execution failed \n" + e));
        LoggerUtils.logErrorToSentry(e);
        codingSubmission.getTestcases()
                .forEach(testcase -> communicationUtils.postResult(new SubmissionResult(codingSubmission.getId(),
                        testcase, SubmissionStatus.RUNTIME_ERROR)));
    }

    private void handleCompilationFailure(CodingSubmission codingSubmission, String compileOutput) {
        log.info(LoggerColors.error("Submission " + codingSubmission.getId() + ": Compilation failed"));
        codingSubmission.getTestcases()
                .forEach(testcase -> communicationUtils.postResult(new SubmissionResult(codingSubmission.getId(),
                        testcase, SubmissionStatus.COMPILE_ERROR)));
        communicationUtils.postOutput(new SubmissionOutput(codingSubmission, "compile", compileOutput));
    }

    private boolean isCompilationFailed(File executable) {
        return executable == null ||
                !executable.exists() ||
                (executable.isDirectory() && isDirectoryEmpty(executable));
    }

    private boolean isDirectoryEmpty(File executable) {
        return Objects.requireNonNull(executable.list()).length == 0;
    }

    private void setupExecutablePermissions(File executable) throws IOException {
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OTHERS_EXECUTE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        perms.add(PosixFilePermission.GROUP_EXECUTE);

        Files.setPosixFilePermissions(executable.toPath(), perms);
    }

    private File createFolder(Submission submission) {
        File submissionFolder = new File("/avogador/" + submission.getId());
        if (submissionFolder.exists()) {
            log.info(LoggerColors.error("Submission " + submission.getId() + ": Already in execution - terminating"));
            throw new RuntimeException("Submission" + submission.getId() + " Already in execution");
        }
        if (submissionFolder.mkdirs()) {
            log.info(LoggerColors.success("Submission " + submission.getId() + ": folder created"));
        }
        return submissionFolder;
    }

    private Pair<File, String> compile(File sourceCode) {
        return languages.stream()
                .filter(language -> language.getSupportedExtension()
                        .equals(FilenameUtils.getExtension(sourceCode.getName())))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Language not supported"))
                .compile(dockerClient, sourceCode);
    }

    void testCaseExecutionWrapper(UUID testcase, CodingSubmission codingSubmission, File executable) {
        SubmissionResult result = new SubmissionResult(codingSubmission, testcase);
        try {
            executeTestCase(executable, testcase, codingSubmission, result);
        } catch (Exception e) {
            log.error(e.toString());
            result.setStatus(SubmissionStatus.RUNTIME_ERROR);
        } finally {
            communicationUtils.postResult(result);
        }
    }

    private void executeTestCase(File executable, UUID testcaseId, CodingSubmission codingSubmission, SubmissionResult submissionResult) throws IOException {
        File input = new File("/avogador/" + codingSubmission.getId() + "/testcases/" + testcaseId + ".in");
        File output = new File("/avogador/" + codingSubmission.getId() + "/testcases/" + testcaseId + ".out");

        String executionOutput = execute(executable, input, codingSubmission, submissionResult).trim();

        if (submissionResult.getStatus() != SubmissionStatus.PENDING) {
            log.info(LoggerColors.purple("Submission " + codingSubmission.getId() +
                    " Testcase " + testcaseId + ": Skipping output check - " + submissionResult.getStatus()));
            communicationUtils.postResult(submissionResult);
            return;
        }

        String expectedOutput = Files.readString(output.toPath(), StandardCharsets.UTF_8).trim();

        log.info(LoggerColors.purple("Submission " + codingSubmission.getId() +
                " Testcase " + testcaseId + " User output:\n" + executionOutput));

        log.info(LoggerColors.cyan("Submission " + codingSubmission.getId() +
                " Testcase " + testcaseId + " Expected output:\n" + expectedOutput));

        communicationUtils.postOutput(new SubmissionOutput(codingSubmission, testcaseId.toString(), executionOutput));
        boolean result = executionOutput.equals(expectedOutput);

        log.info(result ?
                LoggerColors.success("Submission " + codingSubmission.getId() +
                        " Testcase " + testcaseId + ": passed") :
                LoggerColors.error("Submission " + codingSubmission.getId() +
                        " Testcase " + testcaseId + ": reject")
        );

        submissionResult.setStatus(result ? SubmissionStatus.CORRECT : SubmissionStatus.WRONG_ANSWER);
    }

    private String execute(File executable, File inputFile, CodingSubmission codingSubmission, SubmissionResult submissionResult) {
        CreateContainerResponse cExec = languages.stream()
                .filter(l -> l.getName().equals(codingSubmission.getLanguage()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("extension not supported"))
                .configureExecutor(dockerClient, executable, inputFile, codingSubmission);

        dockerClient.startContainerCmd(cExec.getId()).exec();

        try {
            return handleContainerExecution(dockerClient.inspectContainerCmd(cExec.getId())
                    .exec().getId(), codingSubmission, submissionResult);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            dockerClient.removeContainerCmd(cExec.getId()).exec();
        }
    }

    private String handleContainerExecution(String containerId, CodingSubmission codingSubmission, SubmissionResult submissionResult) throws InterruptedException {
        TLEDetector tleDetector = new TLEDetector();

        dockerClient.statsCmd(containerId)
                .exec(tleDetector.getTleChecker(dockerClient, containerId, codingSubmission, submissionResult))
                .awaitCompletion();

        SandboxesUtils.waitContainer(dockerClient, containerId);

        String outputStream = SandboxesUtils.writeContainerLog(dockerClient, containerId, true, false);
        String errorStream = SandboxesUtils.writeContainerLog(dockerClient, containerId, false, true);

        if (tleDetector.wasDetected()) {
            submissionResult.setStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED);
        }

        if (!errorStream.isBlank()) {
            log.info(LoggerColors.error(errorStream));
            if (errorStream.contains("timeout: ")) {
                submissionResult.setStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED);
                communicationUtils.postOutput(new SubmissionOutput(codingSubmission, submissionResult.getTestcaseId().toString(), ""));
            } else {
                submissionResult.setStatus(SubmissionStatus.RUNTIME_ERROR);
                communicationUtils.postOutput(new SubmissionOutput(codingSubmission, submissionResult.getTestcaseId().toString(),
                        errorStream));
            }
        }

        return outputStream;
    }
}
