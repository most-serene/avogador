package eu.mostserene.avogador.executorservice.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        ObjectMapper mapper = new ObjectMapper();
        try {
            sender.send("exercises", "exercises.submission.result",
                    mapper.writeValueAsString(submissionResult));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void postOutput(SubmissionOutput submissionOutput) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            sender.send("storage", "storage.submission.output",
                    mapper.writeValueAsString(submissionOutput));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
