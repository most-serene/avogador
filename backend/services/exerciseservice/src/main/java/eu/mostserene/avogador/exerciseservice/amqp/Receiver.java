package eu.mostserene.avogador.exerciseservice.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.exerciseservice.strox.Strox;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionResult;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionResultDto;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionResultService;
import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import eu.mostserene.avogador.exerciseservice.submissions.SubmissionService;
import eu.mostserene.avogador.exerciseservice.testcases.Testcase;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseDetailDto;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseService;
import eu.mostserene.avogador.exerciseservice.utils.LoggerColors;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
public class Receiver implements MessageListener {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private SubmissionResultService submissionResultService;

    @Autowired
    private TestcaseService testcaseService;

    private void handleMessage(Message message) {
        log.info(message.getMessageProperties().getContentType());
        switch (message.getMessageProperties().getReceivedRoutingKey()) {
            case "exercises.ping." -> log.info(LoggerColors.cyan("Hello from rabbit"));
            case "exercises.submission.save" -> submissionSavedHandler(message);
            case "exercises.submission.result" -> submissionResultHandler(message);
            default -> log.error(LoggerColors.error("call not handled"));
        }
    }

    private void submissionSavedHandler(Message message) {
        try {
            SubmissionSavedDto submissionSavedDto = mapper.readValue(message.getBody(), SubmissionSavedDto.class);

            Submission submission = submissionService.getSubmission(submissionSavedDto.getSubmissionId())
                    .orElseThrow(RuntimeException::new);

            (new Sender()).send("executor", "exec.submission.execute",
                    mapper.writeValueAsString(new SubmissionExecutionDto(
                            submission.getId(),
                            submission.getExercise().getTrial().getCourseId(),
                            submission.getExercise().getTrial().getId(),
                            submission.getExercise().getId(),
                            submission.getExercise().getTrial().getLanguage().name(),
                            submissionSavedDto.getStrox().getSourceFileName(),
                            submission.getExercise().getTimeLimit(),
                            testcaseService.getTestcasesFromExercise(submission.getExercise())
                                    .stream()
                                    .map(TestcaseDetailDto::getId)
                                    .toList()
                    )));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void submissionResultHandler(Message message) {
        try {
            SubmissionResultDto submissionResultDto = mapper.readValue(message.getBody(), SubmissionResultDto.class);
            Submission submission = submissionService.getSubmission(submissionResultDto.getSubmissionId())
                    .orElseThrow(NotFoundException::new);

            Testcase testcase = testcaseService.getSimpleTestcase(submissionResultDto.getTestcaseId())
                    .orElseThrow(NotFoundException::new);

            SubmissionResult savedResult = submissionResultService.saveSubmissionResult(
                    new SubmissionResult(submission, testcase, submissionResultDto.getStatus())
            );
            submissionResultDto.setId(savedResult.getId());
            (new Sender()).send("users", "users.notify.socket", mapper.writeValueAsString(
                    new WebSocketMessage("/" + submissionResultDto.getSubmissionId() + "/results",
                            mapper.writeValueAsString(submissionResultDto)
                    )));
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    @Data
    private static class WebSocketMessage {
        private String topic;
        private String payload;

        public WebSocketMessage() {
        }

        public WebSocketMessage(String topic, String payload) {
            this.topic = topic;
            this.payload = payload;
        }
    }
}