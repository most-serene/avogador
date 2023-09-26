package eu.mostserene.avogador.filesystemservice.exercises;

import eu.mostserene.avogador.filesystemservice.trials.TrialDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExerciseDTO extends TrialDTO {
    private UUID exerciseId;

    public ExerciseDTO() {
    }

    public ExerciseDTO(UUID courseId, UUID trialId, UUID exerciseId) {
        setCourseId(courseId);
        setTrialId(trialId);
        this.exerciseId = exerciseId;
    }
}
