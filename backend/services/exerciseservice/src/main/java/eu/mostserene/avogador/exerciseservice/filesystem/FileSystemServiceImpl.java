package eu.mostserene.avogador.exerciseservice.filesystem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.exerciseservice.amqp.Sender;
import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import lombok.Data;
import org.springframework.stereotype.Service;

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
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public void deleteExercise(Exercise exercise) {
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
}
