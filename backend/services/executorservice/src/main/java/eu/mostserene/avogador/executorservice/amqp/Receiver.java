package eu.mostserene.avogador.executorservice.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.executorservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

@Slf4j
public class Receiver implements MessageListener {
    private static final ObjectMapper mapper = new ObjectMapper();

    private void handleMessage(Message message) {
        log.info(message.getMessageProperties().getContentType());
        switch (message.getMessageProperties().getReceivedRoutingKey()) {
            case "exec.ping." -> log.info(LoggerColors.cyan("Hello from rabbit"));
            default -> log.error(LoggerColors.error("call not handled"));
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