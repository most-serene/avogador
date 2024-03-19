package eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises;

import eu.mostserene.avogador.exerciseservice.abstractexercises.AbstractExercise;
import eu.mostserene.avogador.exerciseservice.abstractexercises.AbstractExerciseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class CodingExerciseDto extends AbstractExerciseDto {
    // FIXME: reactivate this field when enforcing feature in webapp
    // private ProgrammingLanguage language;
    private Integer timeLimit;

    public CodingExerciseDto() {
    }

    public CodingExerciseDto(AbstractExercise abstractExercise) {
        super(abstractExercise.getId(), abstractExercise.getTrial().getId(), abstractExercise.getName(),
                abstractExercise.getStatement(), abstractExercise.getIsVisible());
        if (abstractExercise instanceof CodingExercise) {
            this.timeLimit = ((CodingExercise) abstractExercise).getTimeLimit();
        }
    }

    public CodingExerciseDto(AbstractExerciseDto abstractExerciseDto, Integer timeLimit) {
        super(abstractExerciseDto.getId(), abstractExerciseDto.getTrialId(), abstractExerciseDto.getName(),
                abstractExerciseDto.getStatement(), abstractExerciseDto.getIsVisible());
        this.timeLimit = timeLimit;
    }

    public CodingExerciseDto(UUID id, UUID trialId, String name, String statement, Integer timeLimit, Boolean isVisible) {
        super(id, trialId, name, statement, isVisible);
        this.timeLimit = timeLimit;
        // FIXME: add ProgrammingLanguage field
    }
}
