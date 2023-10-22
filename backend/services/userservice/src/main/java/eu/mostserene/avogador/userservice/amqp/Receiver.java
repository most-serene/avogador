package eu.mostserene.avogador.userservice.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.userservice.utils.LoggerColors;
import eu.mostserene.avogador.userservice.websocket.WebSocketSender;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Slf4j
public class Receiver implements MessageListener {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private WebSocketSender webSocketSender;

    private void handleMessage(Message message) {
        switch (message.getMessageProperties().getReceivedRoutingKey()) {
            case "users.ping." -> log.info(LoggerColors.cyan("Hello from rabbit"));
            case "users.notify.socket" -> handleWebSocketSend(message);
            default -> log.error(LoggerColors.error("call not handled"));
        }
    }

    private void handleWebSocketSend(Message message) {
        try {
            WebSocketMessage webSocketMessage = mapper.readValue(message.getBody(), WebSocketMessage.class);
            webSocketSender.send(webSocketMessage.getTopic(), webSocketMessage.getPayload());
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

    @Data
    private static class WebSocketMessage {
        private String topic;
        private String payload;
    }
}