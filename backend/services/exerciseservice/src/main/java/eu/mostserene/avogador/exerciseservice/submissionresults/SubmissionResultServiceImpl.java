package eu.mostserene.avogador.exerciseservice.submissionresults;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubmissionResultServiceImpl implements SubmissionResultService {
    @Autowired
    private SubmissionResultRepository repository;

    @Override
    public List<SubmissionResult> getResultsFromExerciseAndUser(Exercise exercise, UUID userId) {
        return repository.findBySubmission_Exercise_IdAndSubmission_UserId(exercise.getId(), userId);
    }

    @Override
    public List<SubmissionResult> getResultsFromSubmission(Submission submission) {
        return repository.findBySubmission_Id(submission.getId());
    }

    @Override
    public void saveSubmissionResult(SubmissionResult submissionResult) {
        repository.save(submissionResult);
    }
}
