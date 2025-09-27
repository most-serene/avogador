package eu.mostserene.avogador.courseservice.amqp;


import eu.mostserene.avogador.courseservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Slf4j
public class Receiver  {
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "pingCourses", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "courses", type = ExchangeTypes.TOPIC),
            key = "courses.ping."))
    private void pingCourses() {
        log.info(LoggerColors.cyan("Hello from rabbit"));
    }

}