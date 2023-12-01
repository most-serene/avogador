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
import eu.mostserene.avogador.storageservice.trials.TrialStorageImpl;
import eu.mostserene.avogador.storageservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
public class Receiver implements MessageListener {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private StroxStorage stroxStorage;

    private void handleMessage(Message message) {
        switch (message.getMessageProperties().getReceivedRoutingKey()) {
            case "storage.ping." -> log.info(LoggerColors.cyan("Hello from rabbit"));
            case "storage.course.create" -> courseCreationHandler(message);
            case "storage.trial.create" -> trialCreationHandler(message);
            case "storage.exercise.create" -> exerciseCreationHandler(message);
            case "storage.exercise.similarity" -> exerciseSimilaritySavingHandler(message);
            case "storage.template.create" -> exerciseTemplateCreationHandler(message);
            case "storage.submission.create" -> submissionCreationHandler(message);
            case "storage.submission.output" -> submissionSaveOutputHandler(message);
            case "storage.testcase.create" -> testcaseCreationHandler(message);
            case "storage.testcase.delete" -> testcaseDeleteHandler(message);
            default -> log.error(LoggerColors.error("call not handled"));
        }
    }

    private void courseCreationHandler(Message message) {
        UUID courseId = UUID.fromString(new String(message.getBody(), StandardCharsets.UTF_8));
        CourseStorageImpl.of(courseId).create();
    }

    private void trialCreationHandler(Message message) {
        try {
            TrialDTO trialDTO = mapper.readValue(message.getBody(), TrialDTO.class);
            TrialStorageImpl.of(trialDTO.getCourseId(), trialDTO.getTrialId()).create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void exerciseCreationHandler(Message message) {
        try {
            ExerciseDTO exerciseDTO = mapper.readValue(message.getBody(), ExerciseDTO.class);
            ExerciseStorageImpl.of(exerciseDTO.getCourseId(), exerciseDTO.getTrialId(), exerciseDTO.getExerciseId()).create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void exerciseSimilaritySavingHandler(Message message) {
        try {
            SimilarityReportStorageDto similarityReportStorageDto = mapper.readValue(message.getBody(), SimilarityReportStorageDto.class);
            ExerciseStorageImpl.of(similarityReportStorageDto.getCourseId(), similarityReportStorageDto.getTrialId(),
                    similarityReportStorageDto.getExerciseId())
                    .saveSimilarityReport(similarityReportStorageDto.getSimilarityReportZip());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void exerciseTemplateCreationHandler(Message message) {
        try {
            ExerciseTemplateDTO templateDTO = mapper.readValue(message.getBody(), ExerciseTemplateDTO.class);
            ExerciseStorageImpl.of(templateDTO.getCourseId(), templateDTO.getTrialId(), templateDTO.getExerciseId())
                    .saveTemplate(templateDTO.getTemplate());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void submissionCreationHandler(Message message) {
        try {
            SubmissionDTO submissionDTO = mapper.readValue(message.getBody(), SubmissionDTO.class);
            ExerciseStorageImpl.of(submissionDTO.getCourseId(), submissionDTO.getTrialId(), submissionDTO.getExerciseId())
                    .saveSubmission(submissionDTO.getSubmissionId(), submissionDTO.getSubmission());

            (new Sender()).send("exercises", "exercises.submission.save",
                    mapper.writeValueAsString(new SubmissionSavedDTO(submissionDTO.getSubmissionId(), submissionDTO.getSubmission())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void submissionSaveOutputHandler(Message message) {
        try {
            SubmissionOutputDto submissionDTO = mapper.readValue(message.getBody(), SubmissionOutputDto.class);
            Strox strox = ExerciseStorageImpl.of(submissionDTO.getCourseId(), submissionDTO.getTrialId(), submissionDTO.getExerciseId())
                    .getSubmissionStrox(submissionDTO.getSubmissionId())
                    .orElseThrow(RuntimeException::new);

            strox.getOutputs().put(submissionDTO.getTestcaseId(), submissionDTO.getExecutionOutput());

            stroxStorage.saveToFile(strox);

            /*

            (new Sender()).send("exercises", "exercises.submission.save",
                    mapper.writeValueAsString(new SubmissionSavedDTO(submissionDTO.getSubmissionId(), submissionDTO)));

             */
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void testcaseCreationHandler(Message message) {
        try {
            TestcaseDTO testcaseDTO = mapper.readValue(message.getBody(), TestcaseDTO.class);
            ExerciseStorageImpl.of(testcaseDTO.getCourseId(), testcaseDTO.getTrialId(), testcaseDTO.getExerciseId())
                    .saveTestcase(testcaseDTO.getTestcaseId(), testcaseDTO.getInput(), testcaseDTO.getOutput());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void testcaseDeleteHandler(Message message) {
        try {
            TestcaseDTO testcaseDTO = mapper.readValue(message.getBody(), TestcaseDTO.class);
            ExerciseStorageImpl.of(testcaseDTO.getCourseId(), testcaseDTO.getTrialId(), testcaseDTO.getExerciseId())
                    .deleteTestcase(testcaseDTO.getTestcaseId());
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
}