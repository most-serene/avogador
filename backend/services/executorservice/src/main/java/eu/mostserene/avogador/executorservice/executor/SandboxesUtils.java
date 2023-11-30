package eu.mostserene.avogador.executorservice.executor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.WaitResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SandboxesUtils {

    private SandboxesUtils() {}

    public static void waitContainer(DockerClient dockerClient, String containerId) throws InterruptedException {
        dockerClient.waitContainerCmd(containerId).exec(new ResultCallback.Adapter<>() {
            @Override
            public void onNext(WaitResponse object) {
                super.onNext(object);
            }
        }).awaitCompletion();
    }

    public static String writeContainerLog(DockerClient dockerClient, String containerId, boolean withStdOut, boolean withStdErr) throws InterruptedException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        dockerClient.logContainerCmd(containerId)
                .withStdOut(withStdOut)
                .withStdErr(withStdErr)
                .withFollowStream(false)
                .exec(new ResultCallback.Adapter<>() {
                    @Override
                    public void onNext(Frame object) {
                        super.onNext(object);
                        try {
                            outputStream.write(object.getPayload());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).awaitCompletion();
        return outputStream.toString();
    }
}
