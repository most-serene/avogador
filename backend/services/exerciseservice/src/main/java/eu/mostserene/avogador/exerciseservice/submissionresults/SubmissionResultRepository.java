package eu.mostserene.avogador.exerciseservice.submissionresults;

import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubmissionResultRepository extends JpaRepository<SubmissionResult, UUID> {
    List<SubmissionResult> findBySubmission_Exercise_Id(UUID id);

    List<SubmissionResult> findBySubmission_IdOrderByTestcase_IndexAsc(UUID id);

    List<SubmissionResult> findBySubmission_Exercise_IdAndSubmission_UserIdOrderBySubmission_TimestampAscTestcase_IndexAsc(UUID id, UUID userId);

    void deleteByTestcase_Id(UUID id);

    void deleteBySubmission(Submission submission);
}
