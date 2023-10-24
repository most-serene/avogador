package eu.mostserene.avogador.executorservice.executor.languages;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.WaitResponse;
import eu.mostserene.avogador.executorservice.submission.Submission;
import eu.mostserene.avogador.executorservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
    public File compile(DockerClient dockerClient, File sourceCode) {
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
        try {
            dockerClient.waitContainerCmd(compilerDocker.getId()).exec(new ResultCallback.Adapter<>() {
                @Override
                public void onNext(WaitResponse object) {
                    super.onNext(object);
                }
            }).awaitCompletion();

            InputStream inputStream = dockerClient.copyArchiveFromContainerCmd(compilerDocker.getId(), "/execution")
                    .exec();

            File target = new File(sourceCode.getParentFile() + "/program.tar");
            try {
                FileUtils.copyInputStreamToFile(inputStream, target);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Archiver archiver = ArchiverFactory.createArchiver("tar");
            try {
                archiver.extract(target, new File(target.getParentFile() + "/program"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            dockerClient.removeContainerCmd(compilerDocker.getId()).exec();
        } catch (InterruptedException e) {
            log.error(LoggerColors.error("compilation failed"));
            throw new RuntimeException(e);
        }

        return new File(sourceCode.getParent() + "/program/execution");
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
