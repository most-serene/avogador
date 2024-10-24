package eu.mostserene.avogador.exerciseservice.exercises;


import eu.mostserene.avogador.exerciseservice.exercises.codingexercises.CodingExercise;
import eu.mostserene.avogador.exerciseservice.exercises.codingexercises.CodingExerciseDto;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Getter
@Entity
@Table(name = "Exercises")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @NotNull
    @ManyToOne
    @JoinColumn(name = "trial_id", referencedColumnName = "id")
    private Trial trial;

    @Setter
    @NotNull
    @Length(max = 100)
    private String name;

    @Setter
    @NotNull
    @Length(max = 10000)
    private String statement;

    @Setter
    @NotNull
    private Boolean isVisible = false;

    public Exercise() {

    }

    public Exercise(Trial trial, String name, String statement, Boolean isVisible) {
        this.trial = trial;
        this.name = name;
        this.statement = statement;
        this.isVisible = isVisible;
    }

    public abstract ExerciseType getExerciseType();

    public ExerciseDto toDto() {
        if (getExerciseType().equals(ExerciseType.CODING)) {
            return new CodingExerciseDto((CodingExercise) this);
        }
        return new ExerciseDto(id, trial.getId(), name, statement, isVisible);
    }
}
