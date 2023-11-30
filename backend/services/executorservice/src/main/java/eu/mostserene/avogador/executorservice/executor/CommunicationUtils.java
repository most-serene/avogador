package eu.mostserene.avogador.executorservice.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.executorservice.amqp.Sender;
import eu.mostserene.avogador.executorservice.submission.SubmissionOutput;
import eu.mostserene.avogador.executorservice.submission.SubmissionResult;

public class CommunicationUtils {

    private CommunicationUtils() {
    }

    public static void postResult(SubmissionResult submissionResult) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            (new Sender()).send("exercises", "exercises.submission.result",
                    mapper.writeValueAsString(submissionResult));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void postOutput(SubmissionOutput submissionOutput) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            (new Sender()).send("storage", "storage.submission.output",
                    mapper.writeValueAsString(submissionOutput));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
