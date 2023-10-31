package eu.mostserene.avogador.executorservice.executor.languages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.WaitResponse;
import eu.mostserene.avogador.executorservice.amqp.Sender;
import eu.mostserene.avogador.executorservice.executor.CodeExecutor;
import eu.mostserene.avogador.executorservice.submission.Submission;
import eu.mostserene.avogador.executorservice.submission.SubmissionOutput;
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
import java.nio.charset.StandardCharsets;

@Slf4j
public class JavaLang implements Language {
    @Override
    public String getName() {
        return "JAVA";
    }

    @Override
    public String getSupportedExtension() {
        return "java";
    }

    @Override
    public Pair<File, String> compile(DockerClient dockerClient, File sourceCode) {
        log.info(LoggerColors.warn("Compiling Java: " + sourceCode));
        CreateContainerResponse compilerDocker = dockerClient.createContainerCmd("gotti27/runtime-env:stable")
                .withCmd("/bin/bash", "-c", "javac -d /execution /" + sourceCode.getName())
                //.withCmd("javac", "/" + sourceCode.getName()) // + "; mkdir /program ; mv " +  sourceCode.getName().split("\\.")[0] + ".class " + "/program/" + sourceCode.getName().split("\\.")[0])
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

            InputStream inputStream = dockerClient.copyArchiveFromContainerCmd(compilerDocker.getId(), "/execution")
                    .exec();

            File target = new File(sourceCode.getParentFile() + "/program.tar ");
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

        return Pair.of(new File(sourceCode.getParent() + "/program/execution"), compilerOutputStream.toString(StandardCharsets.UTF_8));
    }

    @Override
    public CreateContainerResponse configureExecutor(DockerClient dockerClient, File executable, File inputFile, Submission submission) {
        log.info(LoggerColors.cyan("Executing " + submission.getId()));
        var container = dockerClient.createContainerCmd("gotti27/runtime-env:stable").withImage("gotti27/runtime-env:stable")//.withUser("student")
                .withCmd("/bin/bash", "-c", "timeout --foreground -k 0 -v " + submission.getTimeLimit() + " java -cp execution Main"  + " < /" + inputFile.getName())
                .withNetworkDisabled(true)
                .exec();

        // java -cp /execution/ Main < /input/" + inputFile.getName()

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
