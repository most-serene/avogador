package eu.mostserene.avogador.exerciseservice.submissionresults;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import eu.mostserene.avogador.exerciseservice.submissions.SubmissionRepository;
import eu.mostserene.avogador.exerciseservice.submissions.SubmissionService;
import eu.mostserene.avogador.exerciseservice.testcases.Testcase;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseService;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubmissionResultServiceImpl implements SubmissionResultService {
    @Autowired
    private SubmissionResultRepository repository;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private TestcaseService testcaseService;
    @Autowired
    private SubmissionRepository submissionRepository;

    @Override
    public List<SubmissionResult> getResultsFromExerciseAndUser(Exercise exercise, UUID userId) {
        return repository.findBySubmission_Exercise_IdAndSubmission_UserId(exercise.getId(), userId);
    }

    @Override
    public List<SubmissionResult> getResultsFromSubmission(Submission submission) {
        return repository.findBySubmission_Id(submission.getId());
    }

    @Override
    public void saveSubmissionResult(SubmissionResultDto submissionResultDto) {
        Submission submission = submissionService.getSubmission(submissionResultDto.getSubmissionId())
                .orElseThrow(NotFoundException::new);

        Testcase testcase = testcaseService.getSimpleTestcase(submissionResultDto.getTestcaseId())
                .orElseThrow(NotFoundException::new);

        SubmissionResult result = new SubmissionResult(submission, testcase, submissionResultDto.getStatus());

        repository.save(result);

        // TODO: notify user via websocket (AVG-281)
    }
}
