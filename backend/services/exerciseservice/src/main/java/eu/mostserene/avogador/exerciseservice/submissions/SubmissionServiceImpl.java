package eu.mostserene.avogador.exerciseservice.submissions;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    @Override
    public Optional<Submission> getSubmission(UUID submissionId) {
        return Optional.empty();
    }

    @Override
    public Submission createSubmission(SubmissionDto submissionDto) {
        return null;
    }

    @Override
    public SubmissionDto exportToDto(Submission submission) {
        return null;
    }
}
