package eu.mostserene.avogador.executorservice.amqp;

import eu.mostserene.avogador.executorservice.executor.CodeExecutor;
import eu.mostserene.avogador.executorservice.submission.Submission;
import eu.mostserene.avogador.executorservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class Receiver {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "pingExecutor"),
            exchange = @Exchange(value = "executor", type = ExchangeTypes.TOPIC),
            key = "exec.ping."))
    private void pingExecutor() {
        log.info(LoggerColors.cyan("Hello from rabbit"));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "executeSubmissionHandler"),
            exchange = @Exchange(value = "executor", type = ExchangeTypes.TOPIC),
            key = "exec.submission.execute")
    )
    private String executeSubmissionHandler(Submission submission) {
            CodeExecutor.getExecutor().checkSubmission(submission);
            return "done";
    }
}