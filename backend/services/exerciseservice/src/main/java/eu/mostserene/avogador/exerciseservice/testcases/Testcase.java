package eu.mostserene.avogador.exerciseservice.testcases;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "Testcases")
public class Testcase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "exercise_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private Exercise exercise;

    @NotNull
    private Boolean isVisible = false;

    @NotNull
    @Min(0)
    private Integer order;

    public Testcase() {
    }

    public Testcase(Exercise exercise, Boolean isVisible, Integer order) {
        this.exercise = exercise;
        this.isVisible = isVisible;
        this.order = order;
    }

    public UUID getId() {
        return id;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean visible) {
        isVisible = visible;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
