package eu.mostserene.avogador.exerciseservice.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.RabbitConverterFuture;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;

@Slf4j
@Service
public class Sender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AsyncRabbitTemplate asyncRabbitTemplate;

    public void send(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

    public <T> void send(String exchange, String routingKey, Object message, BiConsumer<T, Throwable> callback) {
        RabbitConverterFuture<T> rabbitConverterFuture = asyncRabbitTemplate.
                convertSendAndReceive(exchange, routingKey, message);
        rabbitConverterFuture.whenCompleteAsync(callback);
    }
}