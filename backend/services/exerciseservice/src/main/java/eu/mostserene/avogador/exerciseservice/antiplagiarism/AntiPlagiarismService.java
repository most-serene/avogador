package eu.mostserene.avogador.exerciseservice.antiplagiarism;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;

import java.util.Optional;

public interface AntiPlagiarismService {
    void executeSimilarityTool(Exercise exercise);
    Optional<Resource> getSimilarityReport(Exercise exercise);
}
