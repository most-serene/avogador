package eu.mostserene.avogador.exerciseservice.submissionresults;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import eu.mostserene.avogador.exerciseservice.testcases.Testcase;
import eu.mostserene.avogador.exerciseservice.users.UserDto;

import java.util.List;
import java.util.UUID;

public interface SubmissionResultService {
    List<SubmissionResult> getResultsFromExerciseAndUser(Exercise exercise, UUID userId);
    List<SubmissionResult> getResultsFromSubmission(Submission submission);
    void saveSubmissionResult(SubmissionResult submissionResult);
}
