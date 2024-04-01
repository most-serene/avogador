package eu.mostserene.avogador.executorservice.executor.languages;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Statistics;
import eu.mostserene.avogador.executorservice.executor.SandboxesUtils;
import eu.mostserene.avogador.executorservice.executor.TLEDetector;
import eu.mostserene.avogador.executorservice.submission.CodingSubmission;
import eu.mostserene.avogador.executorservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
public class CLang implements Language {
    @Override
    public String getName() {
        return "C";
    }

    @Override
    public String getSupportedExtension() {
        return "c";
    }

    @Override
    public Pair<File, String> compile(DockerClient dockerClient, File sourceCode) {
        log.info(LoggerColors.warn("Compiling c: " + sourceCode));
        CreateContainerResponse compilerDocker = dockerClient.createContainerCmd("gotti27/runtime-env:stable")
                .withCmd("gcc", "-o", "/execution/program", "/" + sourceCode.getName())
                .withNetworkDisabled(true)
                .exec();

        dockerClient.copyArchiveToContainerCmd(compilerDocker.getId())
                .withHostResource(sourceCode.toPath().toString())
                .withRemotePath("/")
                .exec();

        dockerClient.startContainerCmd(compilerDocker.getId()).exec();
        try {
            return runCompilerSandbox(dockerClient, compilerDocker.getId(), sourceCode);
        } catch (InterruptedException | IOException e) {
            log.error(LoggerColors.error("compilation failed"));
            throw new RuntimeException(e);
        } finally {
            dockerClient.removeContainerCmd(compilerDocker.getId()).exec();
        }
    }


    @Override
    public CreateContainerResponse configureExecutor(DockerClient dockerClient, File executable, File inputFile, CodingSubmission codingSubmission) {
        log.info(LoggerColors.cyan("Executing " + codingSubmission.getId()));
        var container = dockerClient.createContainerCmd("gotti27/runtime-env:stable").withImage("gotti27/runtime-env:stable")//.withUser("student")
                .withCmd("/bin/bash", "-c", "chmod 777 /execution/program; timeout --foreground -k 0 -v " + codingSubmission.getTimeLimit() + " ./execution/program" + " < /" + inputFile.getName())
                .withNetworkDisabled(true)
                .exec();

        dockerClient.copyArchiveToContainerCmd(container.getId())
                .withHostResource(executable.toPath().toString())
                .withCopyUIDGID(true)
                .withRemotePath("/")
                .exec();

        dockerClient.copyArchiveToContainerCmd(container.getId())
                .withHostResource(inputFile.toPath().toString())
                .withCopyUIDGID(true)
                .withRemotePath("/")
                .exec();

        return container;
    }

    private Pair<File, String> runCompilerSandbox(DockerClient dockerClient, String containerId, File sourceCode) throws InterruptedException, IOException {
        TLEDetector compileTLDetector = new TLEDetector();

        dockerClient.statsCmd(containerId)
                .exec(getCompilationTimeLimitCallback(dockerClient, containerId, compileTLDetector))
                .awaitCompletion();

        SandboxesUtils.waitContainer(dockerClient, containerId);
        String compilerOutputStream = SandboxesUtils.writeContainerLog(dockerClient, containerId, true, true);

        if (compileTLDetector.wasDetected()) {
            dockerClient.removeContainerCmd(containerId).exec();
            return Pair.of(null, "Compile time exceeded");
        }

        InputStream inputStream = dockerClient.copyArchiveFromContainerCmd(containerId, "/execution")
                .exec();

        File target = new File(sourceCode.getParentFile() + "/program.tar");
        FileUtils.copyInputStreamToFile(inputStream, target);

        Archiver archiver = ArchiverFactory.createArchiver("tar");
        archiver.extract(target, new File(target.getParentFile() + "/program"));

        return Pair.of(new File(sourceCode.getParent() + "/program/execution"), compilerOutputStream);
    }

    private ResultCallback.Adapter<Statistics> getCompilationTimeLimitCallback(DockerClient dockerClient, String containerId, TLEDetector compileTLDetector) {

        return new ResultCallback.Adapter<>() {
            @Override
            public void onNext(Statistics stats) {
                super.onNext(stats);

                if (Objects.requireNonNull(Objects.requireNonNull(stats.getCpuStats().getCpuUsage())
                        .getTotalUsage()) / 1000000000L >= 60) {
                    dockerClient.stopContainerCmd(containerId).withTimeout(0).exec();

                    compileTLDetector.detect();
                    onComplete();
                }
                if (Boolean.FALSE.equals(dockerClient.inspectContainerCmd(containerId).exec().getState().getRunning())) {
                    onComplete();
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
            }
        };
    }
}
