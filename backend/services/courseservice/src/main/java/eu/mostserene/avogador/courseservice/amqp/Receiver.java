package eu.mostserene.avogador.courseservice.amqp;


import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.courseservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

@Slf4j
public class Receiver implements MessageListener {
    private static final ObjectMapper mapper = new ObjectMapper();

    private void handleMessage(Message message) {
        log.info(message.getMessageProperties().getContentType());
        switch (message.getMessageProperties().getReceivedRoutingKey()) {
            case "courses.ping." -> log.info(LoggerColors.error("Hello from rabbit"));
            case "courses.ping.test" -> log.info(LoggerColors.error("Not supported in this version"));
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