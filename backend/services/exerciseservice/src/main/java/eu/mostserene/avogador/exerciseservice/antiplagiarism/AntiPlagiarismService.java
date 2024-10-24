package eu.mostserene.avogador.exerciseservice.antiplagiarism;

import eu.mostserene.avogador.exerciseservice.antiplagiarism.similarityreport.SimilarityReport;
import eu.mostserene.avogador.exerciseservice.exercises.codingexercises.CodingExercise;

import java.util.Optional;

public interface AntiPlagiarismService {
    void executeSimilarityTool(CodingExercise exercise);

    Optional<SimilarityReport> getSimilarityReport(CodingExercise exercise);

    Optional<PlagiarismReport> retrieveSimilarityReportFile(CodingExercise exercise);
}
