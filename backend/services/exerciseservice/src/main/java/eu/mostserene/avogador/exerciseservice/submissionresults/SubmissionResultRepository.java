package eu.mostserene.avogador.exerciseservice.submissionresults;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubmissionResultRepository extends JpaRepository<SubmissionResult, UUID> {
}
