package eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises;

import eu.mostserene.avogador.exerciseservice.abstractexercises.AbstractExerciseDto;
import eu.mostserene.avogador.exerciseservice.trials.ProgrammingLanguage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class CodingExerciseDto extends AbstractExerciseDto {
    private ProgrammingLanguage language;
    private Integer timeLimit;

    public CodingExerciseDto() {
    }

    public CodingExerciseDto(CodingExercise codingExercise) {
        super(codingExercise.getId(), codingExercise.getTrial().getId(), codingExercise.getName(),
                codingExercise.getStatement(), codingExercise.getIsVisible());
        this.timeLimit = codingExercise.getTimeLimit();
        this.language = codingExercise.getLanguage();
    }

    public CodingExerciseDto(AbstractExerciseDto abstractExerciseDto, Integer timeLimit, ProgrammingLanguage language) {
        super(abstractExerciseDto.getId(), abstractExerciseDto.getTrialId(), abstractExerciseDto.getName(),
                abstractExerciseDto.getStatement(), abstractExerciseDto.getIsVisible());
        this.timeLimit = timeLimit;
        this.language = language;
    }

    public CodingExerciseDto(UUID id, UUID trialId, String name, String statement, Integer timeLimit,
                             Boolean isVisible, ProgrammingLanguage language) {
        super(id, trialId, name, statement, isVisible);
        this.timeLimit = timeLimit;
        this.language = language;
    }
}
