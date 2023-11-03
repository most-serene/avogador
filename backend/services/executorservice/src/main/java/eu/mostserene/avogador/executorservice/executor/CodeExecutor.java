package eu.mostserene.avogador.executorservice.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.api.model.WaitResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import eu.mostserene.avogador.executorservice.amqp.Sender;
import eu.mostserene.avogador.executorservice.executor.languages.Language;
import eu.mostserene.avogador.executorservice.storage.StorageService;
import eu.mostserene.avogador.executorservice.submission.Submission;
import eu.mostserene.avogador.executorservice.submission.SubmissionOutput;
import eu.mostserene.avogador.executorservice.submission.SubmissionResult;
import eu.mostserene.avogador.executorservice.submission.SubmissionStatus;
import eu.mostserene.avogador.executorservice.utils.LoggerColors;
import eu.mostserene.avogador.executorservice.utils.LoggerUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.core.env.Environment;
import org.springframework.util.FileSystemUtils;

import java.io.ByteArrayOutputStream;
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
    private DockerClient dockerClient;
    private final Set<Language> languages = new HashSet<>();
    private StorageService storageService;

    @Getter
    private static CodeExecutor executor;

    private CodeExecutor() {
    }

    static void configure(Environment environment, StorageService storageService) {
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
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        executor.dockerClient = DockerClientImpl.getInstance(config, httpClient);

        executor.storageService = storageService;

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

        if ((new File("/avogador")).mkdirs()) {
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
        log.info(LoggerColors.success("> image pulled"));
    }

    public void checkSubmission(Submission submission) {
        log.info(LoggerColors.warn("Submission " + submission.getId() + ": Execution started"));

        File submissionFolder = new File("/avogador/" + submission.getId());
        if (submissionFolder.exists()) {
            log.info(LoggerColors.error("Submission " + submission.getId() + ": Already in execution - terminating"));
            throw new RuntimeException("Submission" + submission.getId() + " Already in execution");
        }
        if (submissionFolder.mkdirs()) {
            log.info(LoggerColors.success("Submission " + submission.getId() + ": folder created"));
        }
        try {

            File code = storageService.fetchAndSaveSubmissionCode(submission);
            File testcases = storageService.fetchAndSaveTestcases(submission);

            Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
            try {
                archiver.extract(code, new File(code.getParentFile() + "/code"));
                archiver.extract(testcases, new File(testcases.getParentFile() + "/testcases"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            File executable = null;
            String compileOutput = null;

            try {
                Pair<File, String> compiled = compile(new File(code.getParentFile() + "/code/" + submission.getFilename()));

                executable = compiled.getLeft();
                compileOutput = compiled.getRight();

                if (executable == null || !executable.exists() || (executable.isDirectory() && Objects.requireNonNull(executable.list()).length == 0)) {
                    log.info(LoggerColors.error("Submission " + submission.getId() + ": Compilation failed"));
                    submission.getTestcases()
                            .forEach(testcase -> postResult(new SubmissionResult(submission.getId(),
                                    testcase, SubmissionStatus.COMPILE_ERROR)));
                    postOutput(new SubmissionOutput(submission, "compile", compileOutput));
                    return;
                }

                Set<PosixFilePermission> perms = new HashSet<>();
                perms.add(PosixFilePermission.OWNER_READ);
                perms.add(PosixFilePermission.OWNER_WRITE);
                perms.add(PosixFilePermission.OTHERS_EXECUTE);
                perms.add(PosixFilePermission.OWNER_EXECUTE);
                perms.add(PosixFilePermission.GROUP_EXECUTE);

                Files.setPosixFilePermissions(executable.toPath(), perms);
            } catch (NotFoundException e) {
                log.info(LoggerColors.error("Submission " + submission.getId() + ": Compilation failed"));
                submission.getTestcases()
                        .forEach(testcase -> postResult(new SubmissionResult(submission.getId(),
                                testcase, SubmissionStatus.COMPILE_ERROR)));
                postOutput(new SubmissionOutput(submission, "compile", compileOutput));
                return;
            }
            log.info(LoggerColors.success("Submission " + submission.getId() + ": Compiled successfully"));
            postOutput(new SubmissionOutput(submission, "compile", compileOutput));

            File finalExecutable = executable;
            submission.getTestcases()
                    .forEach(testcase -> {
                                SubmissionResult result = new SubmissionResult(submission, testcase);

                                try {
                                    executeTestCase(finalExecutable, testcase, submission, result);
                                } catch (Exception e) {
                                    log.error(e.toString());
                                    result.setStatus(SubmissionStatus.RUNTIME_ERROR);
                                } finally {
                                    postResult(result);
                                }
                            }
                    );
            log.info(LoggerColors.success("Submission " + submission.getId() + ": Execution done"));
        } catch (Exception e) {
            log.info(LoggerColors.error("Submission " + submission.getId() + ": Execution failed \n" + e));
            LoggerUtils.logErrorToSentry(e);
            submission.getTestcases()
                    .forEach(testcase -> postResult(new SubmissionResult(submission.getId(),
                            testcase, SubmissionStatus.RUNTIME_ERROR)));
        } finally {
            FileSystemUtils.deleteRecursively(submissionFolder);
            log.info(LoggerColors.success("Submission " + submission.getId() + ": Cleanup completed"));
        }
    }

    private Pair<File, String> compile(File sourceCode) {
        return languages.stream()
                .filter(language -> language.getSupportedExtension()
                        .equals(FilenameUtils.getExtension(sourceCode.getName())))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Language not supported"))
                .compile(dockerClient, sourceCode);
    }

    private void executeTestCase(File executable, UUID testcaseId, Submission submission, SubmissionResult submissionResult) {
        File input = new File("/avogador/" + submission.getId() + "/testcases/" + testcaseId + ".in");
        File output = new File("/avogador/" + submission.getId() + "/testcases/" + testcaseId + ".out");

        String executionOutput = execute(executable, input, submission, submissionResult).trim();

        if (submissionResult.getStatus() != SubmissionStatus.PENDING) {
            log.info(LoggerColors.purple("Submission " + submission.getId() +
                    " Testcase " + testcaseId + ": Skipping output check - " + submissionResult.getStatus()));
            postResult(submissionResult);
            return;
        }

        String expectedOutput = null;
        try {
            expectedOutput = Files.readString(output.toPath(), StandardCharsets.UTF_8).trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info(LoggerColors.purple("Submission " + submission.getId() +
                " Testcase " + testcaseId + " User output:\n" + executionOutput));

        log.info(LoggerColors.cyan("Submission " + submission.getId() +
                " Testcase " + testcaseId + " Expected output:\n" + expectedOutput));

        postOutput(new SubmissionOutput(submission, testcaseId.toString(), executionOutput));
        boolean result = executionOutput.equals(expectedOutput);

        log.info(result ?
                LoggerColors.success("Submission " + submission.getId() +
                        " Testcase " + testcaseId + ": passed") :
                LoggerColors.error("Submission " + submission.getId() +
                        " Testcase " + testcaseId + ": reject")
        );

        submissionResult.setStatus(result ? SubmissionStatus.CORRECT : SubmissionStatus.WRONG_ANSWER);
    }

    private String execute(File executable, File inputFile, Submission submission, SubmissionResult submissionResult) {
        CreateContainerResponse cExec = languages.stream()
                .filter(l -> l.getName().equals(submission.getLanguage()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("extension not supported"))
                .configureExecutor(dockerClient, executable, inputFile, submission);

        dockerClient.startContainerCmd(cExec.getId()).exec();

        InspectContainerResponse res = dockerClient.inspectContainerCmd(cExec.getId()).exec();

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            TLEDetector tleDetector = new TLEDetector();

            dockerClient.statsCmd(cExec.getId()).exec(new ResultCallback.Adapter<>() {
                @Override
                public void onNext(Statistics stats) {
                    super.onNext(stats);

                    if (Objects.requireNonNull(Objects.requireNonNull(stats.getCpuStats().getCpuUsage())
                            .getTotalUsage()) / 1000000000L >= submission.getTimeLimit()) {
                        dockerClient.stopContainerCmd(cExec.getId()).withTimeout(0).exec();
                        log.info(LoggerColors.error("Submission " + submission.getId() +
                                " Testcase " + submissionResult.getTestcaseId() + ": time limit detected"));

                        tleDetector.detect();
                        onComplete();
                    }
                    if (Boolean.FALSE.equals(dockerClient.inspectContainerCmd(cExec.getId()).exec().getState().getRunning())) {
                        onComplete();
                    }
                }

                @Override
                public void onComplete() {
                    super.onComplete();
                }
            }).awaitCompletion();

            dockerClient.waitContainerCmd(cExec.getId()).exec(new ResultCallback.Adapter<>() {
                @Override
                public void onNext(WaitResponse object) {
                    super.onNext(object);
                }
            }).awaitCompletion();

            dockerClient.logContainerCmd(res.getId())
                    .withStdOut(true)
                    .withFollowStream(false)
                    .exec(new ResultCallback.Adapter<>() {
                        @Override
                        public void onNext(Frame object) {
                            super.onNext(object);
                            try {
                                outputStream.write(object.getPayload());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }).awaitCompletion();

            dockerClient.logContainerCmd(res.getId())
                    .withStdErr(true)
                    .withFollowStream(false)
                    .exec(new ResultCallback.Adapter<>() {
                        @Override
                        public void onNext(Frame object) {
                            super.onNext(object);
                            try {
                                errorStream.write(object.getPayload());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }).awaitCompletion();

            if (tleDetector.wasDetected()) {
                submissionResult.setStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED);
            }

            if (!errorStream.toString().isBlank()) {
                log.info(LoggerColors.error(errorStream.toString()));
                if (errorStream.toString(StandardCharsets.UTF_8).contains("timeout: sending signal TERM to command")) {
                    submissionResult.setStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED);
                    postOutput(new SubmissionOutput(submission, submissionResult.getTestcaseId().toString(), ""));
                } else {
                    submissionResult.setStatus(SubmissionStatus.RUNTIME_ERROR);
                    postOutput(new SubmissionOutput(submission, submissionResult.getTestcaseId().toString(),
                            errorStream.toString(StandardCharsets.UTF_8)));
                }
            }

            return outputStream.toString();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            dockerClient.removeContainerCmd(cExec.getId()).exec();
        }
    }

    private void postResult(SubmissionResult submissionResult) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            (new Sender()).send("exercises", "exercises.submission.result",
                    mapper.writeValueAsString(submissionResult));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void postOutput(SubmissionOutput submissionOutput) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            (new Sender()).send("filesystem", "fs.submission.output",
                    mapper.writeValueAsString(submissionOutput));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
