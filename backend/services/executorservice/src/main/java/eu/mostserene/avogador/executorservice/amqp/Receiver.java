package eu.mostserene.avogador.executorservice.amqp;

import eu.mostserene.avogador.executorservice.executor.CodeExecutor;
import eu.mostserene.avogador.executorservice.projectsubmission.ProjectSubmission;
import eu.mostserene.avogador.executorservice.submission.CodingSubmission;
import eu.mostserene.avogador.executorservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

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
    private String executeSubmissionHandler(CodingSubmission codingSubmission) {
        CodeExecutor.getExecutor().checkSubmission(codingSubmission);
        return "done";
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "executeProjectHandler"),
            exchange = @Exchange(value = "executor", type = ExchangeTypes.TOPIC),
            key = "exec.project.execute")
    )
    private void executeProjectHandler(ProjectSubmission projectSubmission) {
        try {
            CodeExecutor.getExecutor().executeProject(projectSubmission);
        } catch (Exception e) {
            log.error(e.toString());
        }
        // return "done";
    }
}