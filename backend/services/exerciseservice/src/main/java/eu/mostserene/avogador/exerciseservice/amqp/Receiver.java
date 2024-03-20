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
import eu.mostserene.avogador.exerciseservice.trials.TrialService;
import eu.mostserene.avogador.exerciseservice.utils.LoggerColors;
import eu.mostserene.avogador.exerciseservice.utils.LoggerUtils;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import eu.mostserene.avogador.exerciseservice.utils.WebSocketMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@Slf4j
@Service
public class Receiver {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private TrialService trialService;

    @Autowired
    private SubmissionResultService submissionResultService;

    @Autowired
    private TestcaseService testcaseService;

    @Autowired
    private Sender sender;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "pingExercises"),
            exchange = @Exchange(value = "exercises", type = ExchangeTypes.TOPIC),
            key = "exercises.ping."))
    private void pingExercises() {
        log.info(LoggerColors.cyan("Hello from rabbit"));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "trialsDeletionHandler"),
            exchange = @Exchange(value = "exercises", type = ExchangeTypes.TOPIC),
            key = "trials.delete"))
    private void trialsDeletionHandler(String courseStringId) {
        UUID courseId = UUID.fromString(courseStringId);
        trialService.deleteTrialsByCourseId(courseId);
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "submissionSavedHandler"),
            exchange = @Exchange(value = "exercises", type = ExchangeTypes.TOPIC),
            key = "exercises.submission.save"))
    private void submissionSavedHandler(SubmissionSavedDto submissionSavedDto) {
        try {
            Submission submission = submissionService.getSubmission(submissionSavedDto.getSubmissionId())
                    .orElseThrow(RuntimeException::new);

            TimeUnit.SECONDS.sleep(1);

            log.info(LoggerColors.warn("hej"));

            sender.send("executor", "exec.submission.execute",
                    new SubmissionExecutionDto(
                            submission.getId(),
                            submission.getExercise().getTrial().getCourseId(),
                            submission.getExercise().getTrial().getId(),
                            submission.getExercise().getId(),
                            submission.getExercise().getLanguage().name(),
                            submissionSavedDto.getStrox().getSourceFileName(),
                            submission.getExercise().getTimeLimit(),
                            testcaseService.getTestcasesFromExercise(submission.getExercise())
                                    .stream()
                                    .map(TestcaseDetailDto::getId)
                                    .toList()
                    ), (BiConsumer<String, Throwable>) (s, throwable) -> {
                        log.info(LoggerColors.success(s));
                        log.info(LoggerColors.error(String.valueOf(throwable)));
                    });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "submissionResultHandler"),
            exchange = @Exchange(value = "exercises", type = ExchangeTypes.TOPIC),
            key = "exercises.submission.result"))
    private void submissionResultHandler(SubmissionResultDto submissionResultDto) {
        try {
            Submission submission = submissionService.getSubmission(submissionResultDto.getSubmissionId())
                    .orElseThrow(NotFoundException::new);

            Testcase testcase = testcaseService.getSimpleTestcase(submissionResultDto.getTestcaseId())
                    .orElseThrow(NotFoundException::new);

            SubmissionResult storedResult = submissionResultService.getResultsFromSubmission(submission)
                    .stream().filter(submissionResult -> submissionResult.getTestcase().getId().equals(testcase.getId()))
                    .findFirst().orElseGet(() -> new SubmissionResult(submission, testcase, submissionResultDto.getStatus()));

            storedResult.setStatus(submissionResultDto.getStatus());
            submissionResultDto.setId(storedResult.getId());
            submissionResultService.saveSubmissionResult(storedResult);

            submissionResultDto.setOutput(null);

            sender.send("users", "users.notify.socket", new WebSocketMessage("/" + submissionResultDto.getSubmissionId() + "/results",
                    mapper.writeValueAsString(submissionResultDto)
            ));
        } catch (IOException e) {
            log.error(LoggerColors.error(e.getMessage()));
            LoggerUtils.logErrorToSentry(e);
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