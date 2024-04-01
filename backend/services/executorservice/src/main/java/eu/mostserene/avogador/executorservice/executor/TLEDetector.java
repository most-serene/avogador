package eu.mostserene.avogador.executorservice.executor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Statistics;
import eu.mostserene.avogador.executorservice.submission.CodingSubmission;
import eu.mostserene.avogador.executorservice.submission.SubmissionResult;
import eu.mostserene.avogador.executorservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class TLEDetector {
    private boolean detected;

    public void detect() {
        detected = true;
    }

    public ResultCallback.Adapter<Statistics> getTleChecker(DockerClient dockerClient, String containerId, CodingSubmission codingSubmission, SubmissionResult submissionResult) {
        return new ResultCallback.Adapter<>() {

            @Override
            public void onNext(Statistics stats) {
                super.onNext(stats);

                if (Objects.requireNonNull(Objects.requireNonNull(stats.getCpuStats().getCpuUsage())
                        .getTotalUsage()) / 1000000000L >= codingSubmission.getTimeLimit()) {
                    dockerClient.stopContainerCmd(containerId).withTimeout(0).exec();
                    log.info(LoggerColors.error("Submission " + codingSubmission.getId() +
                            " Testcase " + submissionResult.getTestcaseId() + ": time limit detected"));

                    detect();
                    onComplete();
                }
                if (Boolean.FALSE.equals(dockerClient.inspectContainerCmd(containerId).exec().getState().getRunning())) {
                    onComplete();
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
            }
        };
    }

    public boolean wasDetected() {
        return detected;
    }
}
