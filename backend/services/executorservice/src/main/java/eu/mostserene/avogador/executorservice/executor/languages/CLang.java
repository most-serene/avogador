package eu.mostserene.avogador.executorservice.executor.languages;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.api.model.WaitResponse;
import eu.mostserene.avogador.executorservice.executor.TLEDetector;
import eu.mostserene.avogador.executorservice.submission.Submission;
import eu.mostserene.avogador.executorservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import java.io.ByteArrayOutputStream;
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
                .withCmd("gcc", "-o", "/execution/program", "/" + sourceCode.getName()) //, "-lstdc++")
                .withNetworkDisabled(true)
                .exec();

        dockerClient.copyArchiveToContainerCmd(compilerDocker.getId())
                //.withHostResource((new File(sourceCode.getParentFile() + "/code")).toPath().toString())
                .withHostResource(sourceCode.toPath().toString())
                .withRemotePath("/")
                .exec();

        dockerClient.startContainerCmd(compilerDocker.getId()).exec();
        ByteArrayOutputStream compilerOutputStream = new ByteArrayOutputStream();
        try {
            TLEDetector compileTLDetector = new TLEDetector();

            dockerClient.statsCmd(compilerDocker.getId()).exec(new ResultCallback.Adapter<>() {
                @Override
                public void onNext(Statistics stats) {
                    super.onNext(stats);

                    if (Objects.requireNonNull(Objects.requireNonNull(stats.getCpuStats().getCpuUsage())
                            .getTotalUsage()) / 1000000000L >= 60) {
                        dockerClient.stopContainerCmd(compilerDocker.getId()).withTimeout(0).exec();

                        compileTLDetector.detect();
                        onComplete();
                    }
                    if (Boolean.FALSE.equals(dockerClient.inspectContainerCmd(compilerDocker.getId()).exec().getState().getRunning())) {
                        onComplete();
                    }
                }

                @Override
                public void onComplete() {
                    super.onComplete();
                }
            }).awaitCompletion();

            dockerClient.waitContainerCmd(compilerDocker.getId()).exec(new ResultCallback.Adapter<>() {
                @Override
                public void onNext(WaitResponse object) {
                    super.onNext(object);
                }
            }).awaitCompletion();

            dockerClient.logContainerCmd(compilerDocker.getId())
                    .withStdOut(true)
                    .withStdErr(true)
                    .withFollowStream(false)
                    .exec(new ResultCallback.Adapter<>() {
                        @Override
                        public void onNext(Frame object) {
                            super.onNext(object);
                            try {
                                compilerOutputStream.write(object.getPayload());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }).awaitCompletion();

            if (compileTLDetector.wasDetected()) {
                dockerClient.removeContainerCmd(compilerDocker.getId()).exec();
                return Pair.of(null, "Compile time exceeded");
            }

            InputStream inputStream = dockerClient.copyArchiveFromContainerCmd(compilerDocker.getId(), "/execution")
                    .exec();

            File target = new File(sourceCode.getParentFile() + "/program.tar");
            FileUtils.copyInputStreamToFile(inputStream, target);

            Archiver archiver = ArchiverFactory.createArchiver("tar");
            archiver.extract(target, new File(target.getParentFile() + "/program"));

            dockerClient.removeContainerCmd(compilerDocker.getId()).exec();
        } catch (InterruptedException | IOException e) {
            log.error(LoggerColors.error("compilation failed"));
            throw new RuntimeException(e);
        }

        return Pair.of( new File(sourceCode.getParent() + "/program/execution"), "");
    }

    @Override
    public CreateContainerResponse configureExecutor(DockerClient dockerClient, File executable, File inputFile, Submission submission) {
        log.info(LoggerColors.cyan("Executing " + submission.getId()));
        var container = dockerClient.createContainerCmd("gotti27/runtime-env:stable").withImage("gotti27/runtime-env:stable")//.withUser("student")
                .withCmd("/bin/bash", "-c", "chmod 777 /execution/program; timeout --foreground -k 0 -v " + submission.getTimeLimit() + " ./execution/program"  + " < /" + inputFile.getName())
                .withNetworkDisabled(true)
                .exec();

        dockerClient.copyArchiveToContainerCmd(container.getId())
                //.withHostResource((new File(sourceCode.getParentFile() + "/code")).toPath().toString())
                .withHostResource(executable.toPath().toString())
                .withCopyUIDGID(true)
                .withRemotePath("/")
                .exec();

        dockerClient.copyArchiveToContainerCmd(container.getId())
                //.withHostResource((new File(sourceCode.getParentFile() + "/code")).toPath().toString())
                .withHostResource(inputFile.toPath().toString())
                .withCopyUIDGID(true)
                .withRemotePath("/")
                .exec();

        return container;
    }


}
