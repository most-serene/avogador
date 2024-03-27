package eu.mostserene.avogador.executorservice.executor.languages;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import eu.mostserene.avogador.executorservice.submission.CodingSubmission;
import eu.mostserene.avogador.executorservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;

@Slf4j
public class Python3Lang implements Language {
    @Override
    public String getName() {
        return "PYTHON";
    }

    @Override
    public String getSupportedExtension() {
        return "py";
    }

    @Override
    public Pair<File, String> compile(DockerClient dockerClient, File sourceCode) {
        return Pair.of(sourceCode, "");
    }

    @Override
    public CreateContainerResponse configureExecutor(DockerClient dockerClient, File executable, File inputFile, CodingSubmission codingSubmission) {
        log.info(LoggerColors.cyan("Executing " + codingSubmission.getId()));
        var container = dockerClient.createContainerCmd("gotti27/runtime-env:stable").withImage("gotti27/runtime-env:stable")//.withUser("student")
                .withCmd("/bin/bash", "-c", "timeout --foreground -k 0 -v " + codingSubmission.getTimeLimit() + " python3 /execution/" + codingSubmission.getFilename() + " < /" + inputFile.getName())
                .withNetworkDisabled(true)
                .exec();

        dockerClient.copyArchiveToContainerCmd(container.getId())
                //.withHostResource((new File(sourceCode.getParentFile() + "/code")).toPath().toString())
                .withHostResource(executable.toPath().toString())
                .withCopyUIDGID(true)
                .withRemotePath("/execution/")
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
