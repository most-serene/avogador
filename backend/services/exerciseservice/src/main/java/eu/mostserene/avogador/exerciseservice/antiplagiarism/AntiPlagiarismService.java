package eu.mostserene.avogador.exerciseservice.antiplagiarism;

import eu.mostserene.avogador.exerciseservice.antiplagiarism.similarityreport.SimilarityReport;
import eu.mostserene.avogador.exerciseservice.exercises.Exercise;

import java.util.Optional;

public interface AntiPlagiarismService {
    void executeSimilarityTool(Exercise exercise);
    Optional<SimilarityReport> getSimilarityReport(Exercise exercise);
    Optional<PlagiarismReport> retrieveSimilarityReportFile(Exercise exercise);
}
