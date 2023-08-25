package eu.mostserene.avogador.filesystemservice.amqp;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class Sender {
    static RabbitTemplate rabbitTemplate;

    static void configure(String rabbitHostname, String username, String password) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitHostname);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        rabbitTemplate = new RabbitTemplate(connectionFactory);
    }


    public void send(String exchange, String routingKey, String message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}