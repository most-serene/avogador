package eu.mostserene.avogador.exerciseservice.submissions;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;

import java.util.Optional;
import java.util.UUID;

public interface SubmissionService {
    Optional<Submission> getSubmission(UUID submissionId);
    Submission createSubmission(Exercise exercise, SubmissionDto submissionDto);

    SubmissionDto exportToDto(Submission submission);
}
