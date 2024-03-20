package eu.mostserene.avogador.exerciseservice.submissions;

import eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises.CodingExercise;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Entity
@Table(name = "Submissions")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "exercise_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    @Setter
    private CodingExercise exercise;

    @NotNull
    @Setter
    private UUID userId;

    @NotNull
    @Setter
    private Date timestamp;

    public Submission() {
    }

    public Submission(CodingExercise exercise, UUID userId, Date timestamp) {
        this.exercise = exercise;
        this.userId = userId;
        this.timestamp = timestamp;
    }
}
