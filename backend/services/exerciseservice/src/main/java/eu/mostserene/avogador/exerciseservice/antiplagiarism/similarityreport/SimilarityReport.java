package eu.mostserene.avogador.exerciseservice.antiplagiarism.similarityreport;

import eu.mostserene.avogador.exerciseservice.exercises.codingexercises.CodingExercise;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Table(name = "SimilarityReports", uniqueConstraints = @UniqueConstraint(columnNames = {"exercise_id"}))
public class SimilarityReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "exercise_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private CodingExercise exercise;

    @NotNull
    private Date timestamp;

    public SimilarityReport() {
    }

    public SimilarityReport(CodingExercise exercise, Date timestamp) {
        this.exercise = exercise;
        this.timestamp = timestamp;
    }

    public void setExercise(CodingExercise exercise) {
        this.exercise = exercise;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
