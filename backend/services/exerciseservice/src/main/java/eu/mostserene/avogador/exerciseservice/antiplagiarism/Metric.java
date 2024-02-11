package eu.mostserene.avogador.exerciseservice.antiplagiarism;

import lombok.Data;

import java.util.List;

@Data
public class Metric {
    private List<Integer> distribution;
    private List<SubmissionComparison> topComparison;

    public Metric() {
    }

    public Metric(List<Integer> distribution, List<SubmissionComparison> topComparison) {
        this.distribution = distribution;
        this.topComparison = topComparison;
    }
}
