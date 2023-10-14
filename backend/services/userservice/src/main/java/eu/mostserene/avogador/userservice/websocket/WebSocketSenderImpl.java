package eu.mostserene.avogador.userservice.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketSenderImpl implements WebSocketSender {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void send(String topic, String payload) {
        simpMessagingTemplate.convertAndSend(topic, payload);
    }
}
