package eu.mostserene.avogador.filesystemservice.exercises;

import eu.mostserene.avogador.filesystemservice.strox.Strox;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExerciseTemplateDTO extends ExerciseDTO {
    private Strox template;

    public ExerciseTemplateDTO() {
    }

    public ExerciseTemplateDTO(UUID courseId, UUID trialId, UUID exerciseId, Strox template) {
        super(courseId, trialId, exerciseId);
        this.template = template;
    }
}
