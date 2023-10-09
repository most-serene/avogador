package eu.mostserene.avogador.executorservice.executor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import eu.mostserene.avogador.executorservice.executor.languages.Language;
import eu.mostserene.avogador.executorservice.storage.StorageService;
import eu.mostserene.avogador.executorservice.submission.Submission;
import eu.mostserene.avogador.executorservice.utils.LoggerColors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class CodeExecutor {
    private DockerClient dockerClient;
    private final Set<Language> languages = new HashSet<>();
    private StorageService storageService;

    @Getter
    private static CodeExecutor executor;

    private CodeExecutor() {}

    static void configure(Environment environment, StorageService storageService) {
        CodeExecutor.executor = new CodeExecutor();

        /*
        DefaultDockerClientConfig config = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                //.withDockerHost(Objects.requireNonNull(environment.getProperty("executor.docker.host")))
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

         */
        executor.storageService = storageService;

        /*
        executor.dockerClient.listContainersCmd().withShowAll(true).exec()
                .stream()
                .peek(container -> log.info(LoggerColors.error(Arrays.toString(container.getNames()))))
                .filter(container -> "gotti27/j-check-env:latest".equals(container.getImage()))
                .peek(container -> log.info(LoggerColors.error(Arrays.toString(container.getNames()))))
                .peek(container -> {
                    if ("running".equals(container.getState())) {
                        executor.dockerClient.killContainerCmd(container.getId()).exec();
                    }
                })
                .forEach(container -> executor.dockerClient.removeContainerCmd(container.getId()).exec());

         */

        File submissionsFolder = new File("/avogador");

        if (submissionsFolder.mkdirs()) {
            log.info(LoggerColors.success("Executor local folder created"));
        }

        new Reflections(new ConfigurationBuilder()
                .forPackages("eu.mostserene.avogador.executorservice.executor.languages"))
                .getSubTypesOf(Language.class)
                .forEach(executor::loadLanguage);

        /*
        try {
            pullImages();
        } catch (InterruptedException e) {
            System.exit(1);
            throw new RuntimeException(e);
        }

         */
    }

    private void loadLanguage(Class<? extends Language> languageType) {
        try {
            Language language = languageType.getDeclaredConstructor().newInstance();
            languages.add(language);
            log.info(LoggerColors.success("> " + language.getName() + " added"));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
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
        log.info(LoggerColors.success("> image pulled"));
    }

    public void executeSubmission(Submission submission) {
        log.info(LoggerColors.warn("Submission " + submission.getId() + ": Execution started"));

        File submissionFolder = new File("/avogador/" + submission.getId());
        if (submissionFolder.mkdirs()) {
            log.info(LoggerColors.success("Submission " + submission.getId() + ": folder created"));
        }

        storageService.fetchAndSaveSubmissionCode(submission);
        storageService.fetchAndSaveTestcases(submission);


/*
        try {
            // TODO: fetch submission code
            // TODO: fetch testcases

            // TODO: compile submission
            // TODO: for each testcase, execute the compiled submission

            // dockerClient.copyArchiveToContainerCmd("").withTarInputStream().exec();

            fetchSubmissionCode(submission);
            fetchTestcases(submission);





            File unzipped = unzipProject(new File(submission.getProjectPath()));

            setStudentPermission(new File(submission.getProjectPath()).getParentFile());

            SubmissionStatus status = executeNotebook(submission.getUserId(), unzipped);
            if (status == SubmissionStatus.PENDING || status == SubmissionStatus.CONFIRMED) {
                throw new IllegalStateException();
            }
            if (Objects.requireNonNull(status) == SubmissionStatus.SUCCESS && !generateHtml(submission.getUserId(), unzipped)) {
                status = SubmissionStatus.ERROR;
            }
            (new ExecutionResult(submission.getUserId(), status)).send();

        } catch (Exception e) {
            (new ExecutionResult(submission.getUserId(), SubmissionStatus.ERROR)).send();
            throw new RuntimeException(e);
        } finally {
            FileSystemUtils.deleteRecursively(
                    new File(new File(submission.getProjectPath()).getParent() + "/execution")
            );
        }

 */
    }
}
