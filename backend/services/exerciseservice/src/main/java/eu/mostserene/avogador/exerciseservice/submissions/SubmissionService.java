package eu.mostserene.avogador.exerciseservice.submissions;

import java.util.Optional;
import java.util.UUID;

public interface SubmissionService {
    Optional<Submission> getSubmission(UUID submissionId);
    Submission createSubmission(SubmissionDto submissionDto);

    SubmissionDto exportToDto(Submission submission);
}
