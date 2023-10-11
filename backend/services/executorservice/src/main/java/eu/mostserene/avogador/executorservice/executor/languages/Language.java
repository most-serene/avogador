package eu.mostserene.avogador.executorservice.executor.languages;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import eu.mostserene.avogador.executorservice.submission.Submission;

import java.io.File;

public interface Language {
    String getName();

    String getSupportedExtension();

    File compile(DockerClient dockerClient, File sourceCode);

    CreateContainerResponse configureExecutor(DockerClient dockerClient, File executable, File inputFile, Submission submission);

}
