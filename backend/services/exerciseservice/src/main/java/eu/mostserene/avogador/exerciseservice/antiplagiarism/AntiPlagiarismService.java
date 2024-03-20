package eu.mostserene.avogador.exerciseservice.antiplagiarism;

import eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises.CodingExercise;
import eu.mostserene.avogador.exerciseservice.antiplagiarism.similarityreport.SimilarityReport;

import java.util.Optional;

public interface AntiPlagiarismService {
    void executeSimilarityTool(CodingExercise exercise);

    Optional<SimilarityReport> getSimilarityReport(CodingExercise exercise);

    Optional<PlagiarismReport> retrieveSimilarityReportFile(CodingExercise exercise);
}
