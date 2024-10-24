package eu.mostserene.avogador.exerciseservice.exercises.codingexercises;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseType;
import eu.mostserene.avogador.exerciseservice.trials.ProgrammingLanguage;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "CodingExercises")
public class CodingExercise extends Exercise {

    @NotNull
    private Integer timeLimit;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProgrammingLanguage language;


    public CodingExercise() {
    }

    public CodingExercise(Trial trial, String name, String statement, Boolean isVisible, Integer timeLimit, ProgrammingLanguage language) {
        super(trial, name, statement, isVisible);
        this.timeLimit = timeLimit;
        this.language = language;
    }

    @Override
    public ExerciseType getExerciseType() {
        return ExerciseType.CODING;
    }

}
