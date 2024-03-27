package eu.mostserene.avogador.executorservice.executor.languages;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import eu.mostserene.avogador.executorservice.submission.CodingSubmission;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;

public interface Language {
    String getName();

    String getSupportedExtension();

    Pair<File, String> compile(DockerClient dockerClient, File sourceCode);

    CreateContainerResponse configureExecutor(DockerClient dockerClient, File executable, File inputFile, CodingSubmission codingSubmission);
}
