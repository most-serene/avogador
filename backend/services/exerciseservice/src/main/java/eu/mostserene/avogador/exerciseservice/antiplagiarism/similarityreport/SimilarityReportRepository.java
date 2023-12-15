package eu.mostserene.avogador.exerciseservice.antiplagiarism.similarityreport;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SimilarityReportRepository extends JpaRepository<SimilarityReport, UUID> {
    Optional<SimilarityReport> findFirstByExercise_Id(UUID id);
}
