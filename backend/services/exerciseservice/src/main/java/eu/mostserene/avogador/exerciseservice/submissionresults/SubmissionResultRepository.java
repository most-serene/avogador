package eu.mostserene.avogador.exerciseservice.submissionresults;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubmissionResultRepository extends JpaRepository<SubmissionResult, UUID> {
    List<SubmissionResult> findBySubmission_Id(UUID id);
    List<SubmissionResult> findBySubmission_Exercise_IdAndSubmission_UserId(UUID id, UUID userId);
}
