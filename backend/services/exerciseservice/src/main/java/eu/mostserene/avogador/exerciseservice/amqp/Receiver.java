package eu.mostserene.avogador.exerciseservice.amqp;


import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.exerciseservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

@Slf4j
public class Receiver implements MessageListener {
    private static final ObjectMapper mapper = new ObjectMapper();

    private void handleMessage(Message message) {
        log.info(message.getMessageProperties().getContentType());
        switch (message.getMessageProperties().getReceivedRoutingKey()) {
            case "exercises.ping." -> log.info(LoggerColors.cyan("Hello from rabbit"));
            default -> log.error(LoggerColors.error("call not handled"));
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            handleMessage(message);
        } catch (Exception e) {
            log.error(e.toString());
            log.error(LoggerColors.error("call not handled"));
        }
    }

    @Data
    private static class SubmissionSavedDto {
        private UUID submissionId;
        private Strox strox;

        public SubmissionSavedDto() {
        }

        public SubmissionSavedDto(UUID submissionId, Strox strox) {
            this.submissionId = submissionId;
            this.strox = strox;
        }
    }

    @Data
    private static class SubmissionExecutionDto {
        private UUID id;
        private UUID courseId;
        private UUID trialId;
        private UUID exerciseId;
        private String language;
        private String filename;
        private Integer timeLimit;
        private List<UUID> testcases;

        public SubmissionExecutionDto() {
        }

        public SubmissionExecutionDto(UUID id, UUID courseId, UUID trialId, UUID exerciseId, String language, String filename, Integer timeLimit, List<UUID> testcases) {
            this.id = id;
            this.courseId = courseId;
            this.trialId = trialId;
            this.exerciseId = exerciseId;
            this.language = language;
            this.filename = filename;
            this.timeLimit = timeLimit;
            this.testcases = testcases;
        }
    }
}