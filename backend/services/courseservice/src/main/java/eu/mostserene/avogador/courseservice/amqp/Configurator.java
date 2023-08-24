package eu.mostserene.avogador.courseservice.amqp;

import eu.mostserene.avogador.courseservice.utils.LoggerColors;
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

import java.util.Objects;

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
        return new Queue("coursesQueue", true);
    }

    @Bean
    Exchange exchange() {
        return new TopicExchange("courses", true, false);
    }

    @Bean
    Binding binding() {
        return new Binding("coursesQueue", Binding.DestinationType.QUEUE, "courses", "courses.#", null);
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
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory
            (MessageConverter contentTypeConverter,
             SimpleRabbitListenerContainerFactoryConfigurer configurator, ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        // the number of consumers is set as 5
        factory.setConcurrentConsumers(5);

        configurator.configure(factory, connectionFactory);
        factory.setMessageConverter(contentTypeConverter);
        return factory;
    }


    @Bean
    public MessageListener messageListener() {
        return new Receiver();
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames("coursesQueue");
        container.setMessageListener(messageListener());
        return container;
    }

}
