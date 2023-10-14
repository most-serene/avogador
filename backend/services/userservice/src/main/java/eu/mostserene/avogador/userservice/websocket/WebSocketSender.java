package eu.mostserene.avogador.userservice.websocket;

public interface WebSocketSender {
    void send(String topic, String payload);
}
