package eu.mostserene.avogador.exerciseservice.submissions;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionService {
    Optional<Submission> getSubmission(UUID submissionId);

    List<Submission> getSubmissionsFromExercise(Exercise exercise);

    List<Submission> getSubmissionsFromExerciseAndUserId(Exercise exercise, UUID userId);

    List<SubmissionDto> getSubmissionDtosFromExerciseAndUserId(Exercise exercise, UUID userId);

    Optional<Submission> getLatestSubmissionFromExerciseAndUserId(Exercise exercise, UUID userId);

    Submission createSubmission(Exercise exercise, SubmissionDto submissionDto);

    SubmissionDto exportToDto(Submission submission);

    /**
     * Warning: this method will not remove the submission storage files in order to prevent overhead while
     * deleting an exercise
     *
     * @param exercise
     */
    void deleteSubmissions(Exercise exercise);
}
