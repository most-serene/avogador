package eu.mostserene.avogador.filesystemservice.amqp;


import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.filesystemservice.courses.CourseStorageImpl;
import eu.mostserene.avogador.filesystemservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
public class Receiver implements MessageListener {
    private static final ObjectMapper mapper = new ObjectMapper();

    private void handleMessage(Message message) {
        log.info(message.getMessageProperties().getContentType());
        switch (message.getMessageProperties().getReceivedRoutingKey()) {
            case "fs.ping." -> log.info(LoggerColors.cyan("Hello from rabbit"));
            case "fs.course.create" -> courseCreationHandler(message);
            default -> log.error(LoggerColors.error("call not handled"));
        }
    }

    private void courseCreationHandler(Message message) {
        UUID courseId = UUID.fromString(new String(message.getBody(), StandardCharsets.UTF_8));
        CourseStorageImpl.of(courseId).create();
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