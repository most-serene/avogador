package eu.mostserene.avogador.executorservice.executor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import eu.mostserene.avogador.executorservice.utils.BookerCatcher;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class PoCLiveSandbox {

    private void performPoC(DockerClient dockerClient) throws Exception {
        LiveSandbox sandbox = new LiveSandbox();
        String containerId = sandbox.startSandbox(dockerClient);
        System.out.println("Container started with ID: " + containerId);

        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("Waiting for client connection...");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket.getInetAddress());

        while (clientSocket.isConnected()) {
            ExecCreateCmdResponse execResponse = dockerClient.execCreateCmd(containerId)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withTty(true)
                    .withCmd("/bin/bash")
                    .exec();

            final BookerCatcher<IOException> catcher = new BookerCatcher<>();
            dockerClient.execStartCmd(execResponse.getId())
                    .withStdIn(clientSocket.getInputStream())
                    .withTty(true)
                    .exec(new ResultCallback.Adapter<>() {
                        @Override
                        public void onNext(Frame frame) {
                            super.onNext(frame);
                            try {
                                clientSocket.getOutputStream().write(frame.getPayload());
                                System.out.println(new String(frame.getPayload(), StandardCharsets.UTF_8));
                            } catch (IOException e) {
                                catcher.catchException(e);
                            }
                        }
                    }).awaitCompletion();

            catcher.throwIfPresent();
        }

        clientSocket.close();
        serverSocket.close();
        sandbox.stopSandbox(dockerClient, containerId);
    }

    public void proofOfConcept(DockerClient dockerClient) {
        try {
            performPoC(dockerClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
