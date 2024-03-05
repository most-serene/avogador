package eu.mostserene.avogador.exerciseservice.submissionresults;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.submissions.Submission;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SubmissionResultService {
    Map<UUID, List<SubmissionResultDto>> getResults(List<Submission> submissions);

    List<SubmissionResult> getResultsFromSubmission(Submission submission);

    SubmissionResult saveSubmissionResult(SubmissionResult submissionResult);

    List<SubmissionResult> getResultsFromExercise(Exercise exercise);

    void deleteSubmissionResultsByTestcaseId(UUID testcaseId);
}
