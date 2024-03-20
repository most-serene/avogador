package eu.mostserene.avogador.exerciseservice.submissionresults;

import eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises.CodingExercise;
import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
public class SubmissionResultServiceImpl implements SubmissionResultService {
    @Autowired
    private SubmissionResultRepository repository;

    @Override
    public Map<UUID, List<SubmissionResultDto>> getResults(List<Submission> submissions) {
        return submissions.stream()
                .collect(Collectors.toMap(
                        Submission::getId,
                        submission -> getResultsFromSubmission(submission)
                                .stream()
                                .map(SubmissionResult::toDto)
                                .toList()
                ));
    }

    @Override
    public List<SubmissionResult> getResultsFromSubmission(Submission submission) {
        return repository.findBySubmission_IdOrderByTestcase_IndexAsc(submission.getId());
    }

    @Override
    public SubmissionResult saveSubmissionResult(SubmissionResult submissionResult) {
        return repository.save(submissionResult);
    }

    @Override
    public List<SubmissionResult> getResultsFromExercise(CodingExercise exercise) {
        return repository.findBySubmission_Exercise_Id(exercise.getId());
    }

    @Override
    public void deleteSubmissionResultsByTestcaseId(UUID testcaseId) {
        repository.deleteByTestcase_Id(testcaseId);
    }

    @Override
    public void deleteSubmissionResultsBySubmission(Submission submission) {
        repository.deleteBySubmission(submission);
    }
}
