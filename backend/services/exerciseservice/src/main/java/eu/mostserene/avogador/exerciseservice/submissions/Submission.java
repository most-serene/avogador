package eu.mostserene.avogador.exerciseservice.submissions;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "Submissions")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "exercise_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private Exercise exercise;

    @NotNull
    private UUID userId;

    @NotNull
    private Date timestamp;

    public Submission() {
    }

    public Submission(Exercise exercise, UUID userId, Date timestamp) {
        this.exercise = exercise;
        this.userId = userId;
        this.timestamp = timestamp;
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
