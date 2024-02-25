package eu.mostserene.avogador.courseservice.utils;

import lombok.Data;

@Data
public class WebSocketMessage {
    private String topic;
    private String payload;

    public WebSocketMessage() {
    }

    public WebSocketMessage(String topic, String payload) {
        this.topic = topic;
        this.payload = payload;
    }
}
