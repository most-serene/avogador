package eu.mostserene.avogador.exerciseservice.submissions;

import eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises.CodingExercise;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionService {
    Optional<Submission> getSubmission(UUID submissionId);

    List<Submission> getSubmissionsFromExercise(CodingExercise exercise);

    List<Submission> getSubmissionsFromExerciseAndUserId(CodingExercise exercise, UUID userId);

    List<SubmissionDto> getSubmissionDtosFromExerciseAndUserId(CodingExercise exercise, UUID userId);

    Optional<Submission> getLatestSubmissionFromExerciseAndUserId(CodingExercise exercise, UUID userId);

    Submission createSubmission(CodingExercise exercise, SubmissionDto submissionDto);

    SubmissionDto exportToDto(Submission submission);

    /**
     * Warning: this method will not remove the submission storage files in order to prevent overhead while
     * deleting an exercise
     *
     * @param exercise
     */
    void deleteSubmissions(CodingExercise exercise);
}
