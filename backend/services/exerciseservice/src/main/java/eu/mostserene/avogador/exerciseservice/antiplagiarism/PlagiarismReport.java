package eu.mostserene.avogador.exerciseservice.antiplagiarism;

import lombok.Data;

import java.util.*;

@Data
public class PlagiarismReport {
    private UUID exerciseId;
    private Date executionDate;
    private Map<UUID, PlagiarismUser> submissions;
    private Map<UUID, Map<UUID, SubmissionComparisonDetail>> comparisons;
    private List<Cluster> clusters;
    private Metric averageMetrics;
    private Metric maxMetrics;

    public PlagiarismReport() {
    }

}
