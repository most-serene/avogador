package eu.mostserene.avogador.exerciseservice.exercises.codingexercises;

import eu.mostserene.avogador.exerciseservice.exercises.ExerciseDto;
import eu.mostserene.avogador.exerciseservice.trials.ProgrammingLanguage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class CodingExerciseDto extends ExerciseDto {
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

    public CodingExerciseDto(ExerciseDto exerciseDto, Integer timeLimit, ProgrammingLanguage language) {
        super(exerciseDto.getId(), exerciseDto.getTrialId(), exerciseDto.getName(),
                exerciseDto.getStatement(), exerciseDto.getIsVisible());
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
