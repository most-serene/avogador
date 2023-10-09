package eu.mostserene.avogador.executorservice.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.executorservice.executor.CodeExecutor;
import eu.mostserene.avogador.executorservice.submission.Submission;
import eu.mostserene.avogador.executorservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.io.IOException;

@Slf4j
public class Receiver implements MessageListener {
    private static final ObjectMapper mapper = new ObjectMapper();

    private void handleMessage(Message message) {
        log.info(message.getMessageProperties().getContentType());
        switch (message.getMessageProperties().getReceivedRoutingKey()) {
            case "exec.ping." -> log.info(LoggerColors.cyan("Hello from rabbit"));
            case "exec.submission.execute" -> executeSubmissionHandler(message);
            default -> log.error(LoggerColors.error("call not handled"));
        }
    }

    private void executeSubmissionHandler(Message message) {
        try {
            Submission submission = mapper.readValue(message.getBody(), Submission.class);
            CodeExecutor.getExecutor().executeSubmission(submission);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            handleMessage(message);
        } catch (Exception e) {
            log.error(e.toString());
            log.error(LoggerColors.error("call not handled"));
        }
    }
}