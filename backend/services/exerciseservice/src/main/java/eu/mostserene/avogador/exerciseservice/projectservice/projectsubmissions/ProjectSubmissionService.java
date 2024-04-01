package eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions;

import java.util.Optional;
import java.util.UUID;

public interface ProjectSubmissionService {
    Optional<ProjectSubmission> getProjectSubmissionById(UUID submissionId);

    ProjectSubmission setProjectSubmissionStatus(UUID submissionStatus, ProjectStatus status);

}
