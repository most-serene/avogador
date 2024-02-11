package eu.mostserene.avogador.storageservice.amqp;


import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.storageservice.courses.CourseStorageImpl;
import eu.mostserene.avogador.storageservice.exercises.ExerciseDTO;
import eu.mostserene.avogador.storageservice.exercises.ExerciseStorageImpl;
import eu.mostserene.avogador.storageservice.exercises.ExerciseTemplateDTO;
import eu.mostserene.avogador.storageservice.exercises.SimilarityReportStorageDto;
import eu.mostserene.avogador.storageservice.strox.Strox;
import eu.mostserene.avogador.storageservice.strox.StroxStorage;
import eu.mostserene.avogador.storageservice.submission.SubmissionDTO;
import eu.mostserene.avogador.storageservice.submission.SubmissionOutputDto;
import eu.mostserene.avogador.storageservice.submission.SubmissionSavedDTO;
import eu.mostserene.avogador.storageservice.testcases.TestcaseDTO;
import eu.mostserene.avogador.storageservice.trials.TrialDTO;
import eu.mostserene.avogador.storageservice.trials.TrialLogDto;
import eu.mostserene.avogador.storageservice.trials.TrialStorageImpl;
import eu.mostserene.avogador.storageservice.utils.LoggerColors;
import eu.mostserene.avogador.storageservice.utils.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class Receiver {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private StroxStorage stroxStorage;

    @Autowired
    private Sender sender;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "pingStorage"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.ping."))
    private void pingStorage() {
        log.info(LoggerColors.cyan("Hello from rabbit"));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "courseCreationHandler"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.course.create"))
    private void courseCreationHandler(String courseStringId) {
        UUID courseId = UUID.fromString(courseStringId);
        CourseStorageImpl.of(courseId).create();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "courseArchivingHandler"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.course.archive"))
    private void courseArchivingHandler(String courseStringId) {
        UUID courseId = UUID.fromString(courseStringId);
        CourseStorageImpl.of(courseId).archive();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "trialCreationHandler"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.trial.create"))
    private void trialCreationHandler(TrialDTO trialDTO) {
        TrialStorageImpl.of(trialDTO.getCourseId(), trialDTO.getTrialId()).create();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "logTrialEventHandler"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.trial.log"))
    private void logTrialEventHandler(TrialLogDto trialLogDto) {
        try {
            TrialStorageImpl.of(trialLogDto.getCourseId(), trialLogDto.getTrialId())
                    .appendLog(trialLogDto.getAvogadorLogMessage());
        } catch (IOException e) {
            log.error(LoggerColors.error(e.getMessage()));
            LoggerUtils.logErrorToSentry(e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "exerciseCreationHandler"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.exercise.create"))
    private void exerciseCreationHandler(ExerciseDTO exerciseDTO) {
        ExerciseStorageImpl.of(exerciseDTO.getCourseId(), exerciseDTO.getTrialId(), exerciseDTO.getExerciseId()).create();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "exerciseSimilaritySavingHandler"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.exercise.similarity"))
    private void exerciseSimilaritySavingHandler(SimilarityReportStorageDto similarityReportStorageDto) {
        ExerciseStorageImpl.of(similarityReportStorageDto.getCourseId(), similarityReportStorageDto.getTrialId(),
                        similarityReportStorageDto.getExerciseId())
                .saveSimilarityReport(similarityReportStorageDto.getSimilarityReport());
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "exerciseTemplateCreationHandler"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.template.create"))
    private void exerciseTemplateCreationHandler(ExerciseTemplateDTO templateDTO) {
        ExerciseStorageImpl.of(templateDTO.getCourseId(), templateDTO.getTrialId(), templateDTO.getExerciseId())
                .saveTemplate(templateDTO.getTemplate());
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "submissionCreationHandler"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.submission.create"))
    private void submissionCreationHandler(SubmissionDTO submissionDTO) {
        ExerciseStorageImpl.of(submissionDTO.getCourseId(), submissionDTO.getTrialId(), submissionDTO.getExerciseId())
                .saveSubmission(submissionDTO.getSubmissionId(), submissionDTO.getSubmission());

        sender.send("exercises", "exercises.submission.save",
                new SubmissionSavedDTO(submissionDTO.getSubmissionId(), submissionDTO.getSubmission()));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "submissionSaveOutputHandler"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.submission.output"))
    private void submissionSaveOutputHandler(SubmissionOutputDto submissionDTO) {
        Strox strox = ExerciseStorageImpl.of(submissionDTO.getCourseId(), submissionDTO.getTrialId(), submissionDTO.getExerciseId())
                .getSubmissionStrox(submissionDTO.getSubmissionId())
                .orElseThrow(RuntimeException::new);
        strox.getOutputs().put(submissionDTO.getTestcaseId(), submissionDTO.getExecutionOutput());
        stroxStorage.saveToFile(strox);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "testcaseCreationHandler"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.testcase.create"))
    private void testcaseCreationHandler(TestcaseDTO testcaseDTO) {
        ExerciseStorageImpl.of(testcaseDTO.getCourseId(), testcaseDTO.getTrialId(), testcaseDTO.getExerciseId())
                .saveTestcase(testcaseDTO.getTestcaseId(), testcaseDTO.getInput(), testcaseDTO.getOutput());
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "testcaseDeleteHandler"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.testcase.delete"))
    private void testcaseDeleteHandler(TestcaseDTO testcaseDTO) {
        ExerciseStorageImpl.of(testcaseDTO.getCourseId(), testcaseDTO.getTrialId(), testcaseDTO.getExerciseId())
                .deleteTestcase(testcaseDTO.getTestcaseId());
    }

}