package eu.mostserene.avogador.executorservice.amqp;

import eu.mostserene.avogador.executorservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@Slf4j
public class Configurator {
    @Autowired
    private Environment environment;

    @Value("${spring.rabbitmq.host}")
    private String rabbitHostname;

    @Value("${spring.rabbitmq.username}")
    private String rabbitUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitPassword;

    @Bean
    Queue queue() {
        return new Queue("executorQueue", true);
    }

    @Bean
    Exchange exchange() {
        return new TopicExchange("executor", true, false);
    }

    @Bean
    Binding binding() {
        return new Binding("executorQueue", Binding.DestinationType.QUEUE, "executor", "exec.#", null);
    }

    @Bean
    Receiver receiver() {
        return new Receiver();
    }

    @Bean
    void configureSender() {
        Sender.configure(rabbitHostname, rabbitUsername, rabbitPassword);
        log.info(LoggerColors.success("|-- Sender Configured --|"));
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitHostname);
        connectionFactory.setUsername(rabbitUsername);
        connectionFactory.setPassword(rabbitPassword);
        return connectionFactory;
    }

    @Bean
    MessageConverter contentTypeConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public MessageListener messageListener() {
        return new Receiver();
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConcurrentConsumers(5);
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames("executorQueue");
        container.setMessageListener(messageListener());
        return container;
    }

}
