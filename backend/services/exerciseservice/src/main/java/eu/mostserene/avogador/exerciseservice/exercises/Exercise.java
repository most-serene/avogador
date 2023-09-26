package eu.mostserene.avogador.exerciseservice.exercises;

import eu.mostserene.avogador.exerciseservice.trials.Trial;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "Exercises")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "trial_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private Trial trial;

    @NotNull
    private String name;

    @NotNull
    private String statement;

    @NotNull
    private Integer timeLimit;

    @NotNull
    private Boolean isVisible = false;

    public Exercise() {
    }

    public Exercise(Trial trial, String name, String statement, Integer timeLimit) {
        this.trial = trial;
        this.name = name;
        this.statement = statement;
        this.timeLimit = timeLimit;
    }

    public Exercise(Trial trial, String name, String statement, Integer timeLimit, Boolean isVisible) {
        this.trial = trial;
        this.name = name;
        this.statement = statement;
        this.timeLimit = timeLimit;
        this.isVisible = isVisible;
    }

    public UUID getId() {
        return id;
    }

    public Trial getTrial() {
        return trial;
    }

    public void setTrial(Trial trial) {
        this.trial = trial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean visible) {
        isVisible = visible;
    }

    public ExerciseDto toDto(){
        return new ExerciseDto(id, trial.getId(), name, statement, timeLimit, isVisible);
    }
}
