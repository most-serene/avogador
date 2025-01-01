package eu.mostserene.avogador.exerciseservice.exercises.multiplechoiceexercises;

import eu.mostserene.avogador.exerciseservice.exercises.ExerciseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MultipleChoiceExerciseDto extends ExerciseDto {
    private Boolean hasMultipleAnswers;
    private Double correctPoints;
    private Double wrongPoints;
    private Boolean strictMode;
    private Boolean hasShuffling;
    private List<MultipleChoiceOptionDto> options = List.of();

    public MultipleChoiceExerciseDto() {
    }

    public MultipleChoiceExerciseDto(MultipleChoiceExercise exercise) {
        super(exercise.getId(), exercise.getTrial().getId(), exercise.getName(),
                exercise.getStatement(), exercise.getIsVisible());
        this.hasMultipleAnswers = exercise.getHasMultipleAnswers();
        this.correctPoints = exercise.getCorrectPoints();
        this.wrongPoints = exercise.getWrongPoints();
        this.strictMode = exercise.getStrictMode();
        this.hasShuffling = exercise.getHasShuffling();
    }
}
