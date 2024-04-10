package eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions;

import eu.mostserene.avogador.exerciseservice.projectservice.projects.Project;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectSubmissionService {
    ProjectSubmission createSubmission(Project project, UserDto user, MultipartFile file);

    Optional<ProjectSubmission> getProjectSubmissionById(UUID submissionId);

    ProjectSubmission setProjectSubmissionStatus(UUID submissionStatus, ProjectStatus status);

    List<ProjectSubmission> getUserSubmissions(Project project, UUID userId);

    List<ProjectSubmission> getUsersLastProjectSubmissions(Project project, List<UUID> users);

}
