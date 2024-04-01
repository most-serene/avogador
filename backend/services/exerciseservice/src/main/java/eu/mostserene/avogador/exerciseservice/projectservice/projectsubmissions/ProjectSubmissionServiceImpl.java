package eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions;

import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class ProjectSubmissionServiceImpl implements ProjectSubmissionService {

    @Autowired
    private ProjectSubmissionRepository repository;

    @Override
    public Optional<ProjectSubmission> getProjectSubmissionById(UUID submissionId) {
        return repository.findById(submissionId);
    }

    @Override
    public ProjectSubmission setProjectSubmissionStatus(UUID submissionId, ProjectStatus status) {
        ProjectSubmission submission = getProjectSubmissionById(submissionId)
                .orElseThrow(NotFoundException::new);

        submission.setStatus(status);

        return repository.save(submission);
    }
}
