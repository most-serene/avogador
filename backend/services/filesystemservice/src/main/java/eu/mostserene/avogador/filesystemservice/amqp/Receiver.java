package eu.mostserene.avogador.filesystemservice.amqp;


import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.filesystemservice.courses.CourseStorageImpl;
import eu.mostserene.avogador.filesystemservice.exercises.ExerciseDTO;
import eu.mostserene.avogador.filesystemservice.exercises.ExerciseStorageImpl;
import eu.mostserene.avogador.filesystemservice.exercises.ExerciseTemplateDTO;
import eu.mostserene.avogador.filesystemservice.submission.SubmissionDTO;
import eu.mostserene.avogador.filesystemservice.submission.SubmissionSavedDTO;
import eu.mostserene.avogador.filesystemservice.testcases.TestcaseDTO;
import eu.mostserene.avogador.filesystemservice.trials.TrialDTO;
import eu.mostserene.avogador.filesystemservice.trials.TrialStorageImpl;
import eu.mostserene.avogador.filesystemservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
public class Receiver implements MessageListener {
    private static final ObjectMapper mapper = new ObjectMapper();

    private void handleMessage(Message message) {
        switch (message.getMessageProperties().getReceivedRoutingKey()) {
            case "fs.ping." -> log.info(LoggerColors.cyan("Hello from rabbit"));
            case "fs.course.create" -> courseCreationHandler(message);
            case "fs.trial.create" -> trialCreationHandler(message);
            case "fs.exercise.create" -> exerciseCreationHandler(message);
            case "fs.template.create" -> exerciseTemplateCreationHandler(message);
            case "fs.submission.create" -> submissionCreationHandler(message);
            case "fs.testcase.create" -> testcaseCreationHandler(message);
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

    private void testcaseCreationHandler(Message message) {
        try {
            TestcaseDTO testcaseDTO = mapper.readValue(message.getBody(), TestcaseDTO.class);
            ExerciseStorageImpl.of(testcaseDTO.getCourseId(), testcaseDTO.getTrialId(), testcaseDTO.getExerciseId())
                    .saveTestcase(testcaseDTO.getTestcaseId(), testcaseDTO.getInput(), testcaseDTO.getOutput());
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