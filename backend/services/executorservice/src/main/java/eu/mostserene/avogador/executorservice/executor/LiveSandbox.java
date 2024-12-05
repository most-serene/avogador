package eu.mostserene.avogador.executorservice.executor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import eu.mostserene.avogador.executorservice.utils.BookerCatcher;
import org.apache.commons.lang3.NotImplementedException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LiveSandbox {

    public String startSandbox(DockerClient dockerClient) {
        CreateContainerResponse container = dockerClient.createContainerCmd("gotti27/runtime-env:stable")
                .withImage("gotti27/runtime-env:stable")
                .withCmd("/bin/bash")
                // .withNetworkDisabled(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(true)
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();

        return container.getId();
    }

    public void stopSandbox(DockerClient dockerClient, String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
        dockerClient.removeContainerCmd(containerId).exec();
    }

    public String sendCommand(DockerClient dockerClient, String containerId, String command) throws InterruptedException, IOException {
        ExecCreateCmdResponse execResponse = dockerClient.execCreateCmd(containerId)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(true)
                .withCmd("bash", "-i")
                .exec();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        final BookerCatcher<IOException> catcher = new BookerCatcher<>();
        dockerClient.execStartCmd(execResponse.getId())
                .exec(new ResultCallback.Adapter<>() {
                    @Override
                    public void onNext(Frame frame) {
                        super.onNext(frame);
                        try {
                            outputStream.write(frame.getPayload());
                        } catch (IOException e) {
                            catcher.catchException(e);
                        }
                    }
                }).awaitCompletion();

        catcher.throwIfPresent();
        return outputStream.toString();
    }

    public String retrieveOutput(String containerId) {
        throw new NotImplementedException("Still not implemented");
        // ask storage for log
    }
}
