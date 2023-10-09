package eu.mostserene.avogador.executorservice.executor.languages;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;

import java.io.File;

public interface Language {
    String getName();

    String getSupportedExtension();

    File compile(DockerClient dockerClient, String mountingPoint, final File sourceCode);

    //CreateContainerCmd configureExecutor(CreateContainerCmd createContainerCmd, String mountingPoint, File executable, Exercise exercise, File inputFile);

}
