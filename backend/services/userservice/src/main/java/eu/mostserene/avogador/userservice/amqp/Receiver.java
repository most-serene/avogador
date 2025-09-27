package eu.mostserene.avogador.userservice.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.userservice.utils.LoggerColors;
import eu.mostserene.avogador.userservice.websocket.WebSocketSender;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class Receiver {

    @Autowired
    private WebSocketSender webSocketSender;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "pingUsers", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "users", type = ExchangeTypes.TOPIC),
            key = "users.ping."))
    private void pingUsers() {
        log.info(LoggerColors.cyan("Hello from rabbit"));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "handleWebSocketSend", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "users", type = ExchangeTypes.TOPIC),
            key = "users.notify.socket")
    )
    private void handleWebSocketSend(WebSocketMessage webSocketMessage) {
        webSocketSender.send(webSocketMessage.getTopic(), webSocketMessage.getPayload());
    }

    @Data
    private static class WebSocketMessage {
        private String topic;
        private String payload;
    }
}