package eu.mostserene.avogador.exerciseservice.submissions;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import eu.mostserene.avogador.exerciseservice.strox.Strox;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionResultService;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private SubmissionResultService submissionResultService;

    @Override
    public Optional<Submission> getSubmission(UUID submissionId) {
        return submissionRepository.findById(submissionId);
    }

    @Override
    public List<Submission> getSubmissionsFromExercise(Exercise exercise) {
        return submissionRepository.findByExercise_IdOrderByTimestampAsc(exercise.getId());
    }

    @Override
    public List<Submission> getSubmissionsFromExerciseAndUserId(Exercise exercise, UUID userId) {
        return submissionRepository.findByExercise_IdAndUserIdOrderByTimestampDesc(exercise.getId(), userId);
    }

    @Override
    public List<SubmissionDto> getSubmissionDtosFromExerciseAndUserId(Exercise exercise, UUID userId) {
        var submissions = getSubmissionsFromExerciseAndUserId(exercise, userId);

        return submissions.stream()
                .map(submission -> new SubmissionDto(submission.getId(),
                                submission.getExercise().getId(),
                                userId,
                                submission.getTimestamp(),
                                storageService.getSubmissionStrox(submission)
                                        .orElseThrow(() -> new NotFoundException(submission.getId() + " Strox not saved"))
                                        .getCells()
                        )
                )
                .toList();
    }

    @Override
    public Optional<Submission> getLatestSubmissionFromExerciseAndUserId(Exercise exercise, UUID userId) {
        return submissionRepository.findFirstByExercise_IdAndUserIdOrderByTimestampDesc(exercise.getId(), userId);
    }

    @Override
    public Submission createSubmission(Exercise exercise, SubmissionDto submissionDto) {
        Submission submission = submissionRepository.save(new Submission(
                exercise, submissionDto.getUserId(), Date.from(Instant.now())
        ));

        Strox strox = new Strox();
        strox.setCells(submissionDto.getStroxCells().stream().toList());

        storageService.createSubmission(submission, strox);
        return submission;
    }

    @Override
    public SubmissionDto exportToDto(Submission submission) {
        Strox strox = storageService.getMergedSubmission(submission)
                .orElseThrow(() -> new RuntimeException("Template or Submission not existing in storage"));

        return new SubmissionDto(submission.getId(), submission.getExercise().getId(),
                submission.getUserId(), submission.getTimestamp(), strox.getCells());
    }

    @Override
    public void deleteSubmissions(Exercise exercise) {
        submissionRepository.findByExercise_IdOrderByTimestampAsc(exercise.getId())
                .forEach(submission -> {
                    submissionResultService.deleteSubmissionResultsBySubmission(submission);
                    submissionRepository.delete(submission);
                });
    }
}
