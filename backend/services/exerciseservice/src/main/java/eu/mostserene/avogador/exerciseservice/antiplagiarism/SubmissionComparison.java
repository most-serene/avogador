package eu.mostserene.avogador.exerciseservice.antiplagiarism;

import lombok.Data;

import java.util.UUID;

@Data
public class SubmissionComparison {
    private UUID first;
    private UUID second;
    private double similarity;

    public SubmissionComparison() {
    }

    public SubmissionComparison(UUID first, UUID second, double similarity) {
        this.first = first;
        this.second = second;
        this.similarity = similarity;
    }
}
