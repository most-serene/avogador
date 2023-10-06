package eu.mostserene.avogador.exerciseservice.filesystem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.exerciseservice.amqp.Sender;
import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseDetailDto;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseIODto;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class FileSystemServiceImpl implements FileSystemService {

    @Override
    public void createTrial(Trial trial) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            (new Sender())
                    .send("filesystem", "fs.trial.create", mapper.writeValueAsString(new TrialStorageDTO(trial.getCourseId(), trial.getId())));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTrial(Trial trial) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public void createExercise(Exercise exercise) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            (new Sender())
                    .send("filesystem", "fs.exercise.create",
                            mapper.writeValueAsString(new ExerciseStorageDTO(
                                    exercise.getTrial().getCourseId(), exercise.getTrial().getId(), exercise.getId())));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteExercise(Exercise exercise) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public void createTestcase(TestcaseDetailDto testcase) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public void deleteTestcase(TestcaseDetailDto testcase) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public Optional<TestcaseIODto> getTestcase(UUID testcaseId) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public void updateTestcase(TestcaseDetailDto testcase) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Data
    private static class TrialStorageDTO {
        private UUID courseId;
        private UUID trialId;

        public TrialStorageDTO() {
        }

        public TrialStorageDTO(UUID courseId, UUID trialId) {
            this.courseId = courseId;
            this.trialId = trialId;
        }
    }

    @Data
    private static class ExerciseStorageDTO {
        private UUID courseId;
        private UUID trialId;

        private UUID exerciseId;

        public ExerciseStorageDTO() {
        }

        public ExerciseStorageDTO(UUID courseId, UUID trialId, UUID exerciseId) {
            this.courseId = courseId;
            this.trialId = trialId;
            this.exerciseId = exerciseId;
        }
    }
}
