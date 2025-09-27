package eu.mostserene.avogador.storageservice.amqp;


import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.storageservice.courses.CourseStorageImpl;
import eu.mostserene.avogador.storageservice.exercises.ExerciseDTO;
import eu.mostserene.avogador.storageservice.exercises.ExerciseStorageImpl;
import eu.mostserene.avogador.storageservice.exercises.ExerciseTemplateDTO;
import eu.mostserene.avogador.storageservice.exercises.SimilarityReportStorageDto;
import eu.mostserene.avogador.storageservice.projects.ProjectDTO;
import eu.mostserene.avogador.storageservice.projects.ProjectStorageImpl;
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
            value = @Queue(value = "pingStorage", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.ping."))
    private void pingStorage() {
        log.info(LoggerColors.cyan("Hello from rabbit"));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "courseCreationHandler", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.course.create"))
    private void courseCreationHandler(String courseStringId) {
        UUID courseId = UUID.fromString(courseStringId);
        CourseStorageImpl.of(courseId).create();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "courseDeletionHandler", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.course.delete"))
    private void courseDeletionHandler(String courseStringId) {
        UUID courseId = UUID.fromString(courseStringId);
        CourseStorageImpl.of(courseId).delete();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "courseArchivingHandler", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.course.archive"))
    private boolean courseArchivingHandler(String courseStringId) {
        UUID courseId = UUID.fromString(courseStringId);
        return CourseStorageImpl.of(courseId).archive();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "trialCreationHandler", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.trial.create"))
    private void trialCreationHandler(TrialDTO trialDTO) {
        TrialStorageImpl.of(trialDTO.getCourseId(), trialDTO.getTrialId()).create();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "trialDeletionHandler", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.trial.delete"))
    private void trialDeletionHandler(TrialDTO trialDTO) {
        TrialStorageImpl.of(trialDTO.getCourseId(), trialDTO.getTrialId()).delete();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "logTrialEventHandler", durable = "true", autoDelete = "false"),
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
            value = @Queue(value = "exerciseCreationHandler", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.exercise.create"))
    private void exerciseCreationHandler(ExerciseDTO exerciseDTO) {
        ExerciseStorageImpl.of(exerciseDTO.getCourseId(), exerciseDTO.getTrialId(), exerciseDTO.getExerciseId()).create();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "exerciseDeletionHandler", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.exercise.delete"))
    private void exerciseDeletionHandler(ExerciseDTO exerciseDTO) {
        ExerciseStorageImpl.of(exerciseDTO.getCourseId(), exerciseDTO.getTrialId(), exerciseDTO.getExerciseId()).delete();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "exerciseSimilaritySavingHandler", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.exercise.similarity"))
    private void exerciseSimilaritySavingHandler(SimilarityReportStorageDto similarityReportStorageDto) {
        ExerciseStorageImpl.of(similarityReportStorageDto.getCourseId(), similarityReportStorageDto.getTrialId(),
                        similarityReportStorageDto.getExerciseId())
                .saveSimilarityReport(similarityReportStorageDto.getSimilarityReport());
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "exerciseTemplateCreationHandler", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.template.create"))
    private void exerciseTemplateCreationHandler(ExerciseTemplateDTO templateDTO) {
        ExerciseStorageImpl.of(templateDTO.getCourseId(), templateDTO.getTrialId(), templateDTO.getExerciseId())
                .saveTemplate(templateDTO.getTemplate());
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "submissionCreationHandler", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.submission.create"))
    private void submissionCreationHandler(SubmissionDTO submissionDTO) {
        ExerciseStorageImpl.of(submissionDTO.getCourseId(), submissionDTO.getTrialId(), submissionDTO.getExerciseId())
                .saveSubmission(submissionDTO.getSubmissionId(), submissionDTO.getSubmission());

        sender.send("exercises", "exercises.submission.save",
                new SubmissionSavedDTO(submissionDTO.getSubmissionId(), submissionDTO.getSubmission()));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "submissionSaveOutputHandler", durable = "true", autoDelete = "false"),
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
            value = @Queue(value = "testcaseCreationHandler", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.testcase.create"))
    private void testcaseCreationHandler(TestcaseDTO testcaseDTO) {
        ExerciseStorageImpl.of(testcaseDTO.getCourseId(), testcaseDTO.getTrialId(), testcaseDTO.getExerciseId())
                .saveTestcase(testcaseDTO.getTestcaseId(), testcaseDTO.getInput(), testcaseDTO.getOutput());
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "testcaseDeleteHandler", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.testcase.delete"))
    private void testcaseDeleteHandler(TestcaseDTO testcaseDTO) {
        ExerciseStorageImpl.of(testcaseDTO.getCourseId(), testcaseDTO.getTrialId(), testcaseDTO.getExerciseId())
                .deleteTestcase(testcaseDTO.getTestcaseId());
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "projectCreationHandler", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "storage", type = ExchangeTypes.TOPIC),
            key = "storage.project.create"))
    private void projectCreationHandler(ProjectDTO projectDTO) {
        ProjectStorageImpl.of(projectDTO.getCourseId(), projectDTO.getProjectId())
                .create();
    }

}