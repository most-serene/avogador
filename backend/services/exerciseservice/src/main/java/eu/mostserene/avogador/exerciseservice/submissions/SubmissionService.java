package eu.mostserene.avogador.exerciseservice.submissions;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionService {
    Optional<Submission> getSubmission(UUID submissionId);
    List<Submission> getSubmissionsFromExerciseAndUserId(Exercise exercise, UUID userId);
    Optional<Submission> getLatestSubmissionFromExerciseAndUserId(Exercise exercise, UUID userId);
    Submission createSubmission(Exercise exercise, SubmissionDto submissionDto);
    SubmissionDto exportToDto(Submission submission);
}
