package eu.mostserene.avogador.executorservice.executor;

import eu.mostserene.avogador.executorservice.amqp.Sender;
import eu.mostserene.avogador.executorservice.submission.SubmissionOutput;
import eu.mostserene.avogador.executorservice.submission.SubmissionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommunicationUtils {
    @Autowired
    private Sender sender;

    private CommunicationUtils() {
    }

    public void postResult(SubmissionResult submissionResult) {
        sender.send("exercises", "exercises.submission.result",
                submissionResult);
    }

    public void postOutput(SubmissionOutput submissionOutput) {
        sender.send("storage", "storage.submission.output",
                submissionOutput);
    }
}
