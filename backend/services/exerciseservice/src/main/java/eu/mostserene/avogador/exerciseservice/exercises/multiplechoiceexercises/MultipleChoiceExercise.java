package eu.mostserene.avogador.exerciseservice.exercises.multiplechoiceexercises;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseType;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MultipleChoiceExercises")
public class MultipleChoiceExercise extends Exercise {
    @NotNull
    private Boolean hasMultipleAnswers = false;

    @NotNull
    private Double correctPoints = 1.;

    @NotNull
    private Double wrongPoints = 0.;

    @NotNull
    private Boolean strictMode = false;

    @NotNull
    private Boolean hasShuffling = false;

    public MultipleChoiceExercise() {

    }

    public MultipleChoiceExercise(Trial trial, String name, String statement, Boolean isVisible, Boolean hasMultipleAnswers, Double correctPoints, Double wrongPoints, Boolean strictMode, Boolean hasShuffling) {
        super(trial, name, statement, isVisible);
        this.hasMultipleAnswers = hasMultipleAnswers;
        this.correctPoints = correctPoints;
        this.wrongPoints = wrongPoints;
        this.strictMode = strictMode;
        this.hasShuffling = hasShuffling;
    }

    @Override
    public ExerciseType getExerciseType() {
        return ExerciseType.MULTIPLE_CHOICE;
    }
}
