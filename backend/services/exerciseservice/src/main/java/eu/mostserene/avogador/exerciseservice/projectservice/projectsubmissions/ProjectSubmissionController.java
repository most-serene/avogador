package eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions;

import eu.mostserene.avogador.exerciseservice.courses.CourseDetailDto;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.projectservice.projects.Project;
import eu.mostserene.avogador.exerciseservice.projectservice.projects.ProjectService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/public/projects/{projectId}/submissions")
public class ProjectSubmissionController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectSubmissionService projectSubmissionService;

    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private StorageService storageService;

    @GetMapping("/{submissionId}")
    private ProjectSubmission getProjectSubmission(@RequestHeader(name = "User") UserDto user,
                                                   @PathVariable UUID projectId,
                                                   @PathVariable UUID submissionId) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(NotFoundException::new);

        CourseDetailDto course = userCourseService.getUserCourseRoleDetail(project.getCourseId(), user.getId())
                .orElseThrow(NotFoundException::new);

        ProjectSubmission submission = projectSubmissionService.getProjectSubmissionById(submissionId)
                .orElseThrow(NotFoundException::new);

        if (!user.getId().equals(submission.getUserId()) &&
                !course.getRole().hasCollaboratorClearance() &&
                !user.getIsSuperuser()) {
            throw new ForbiddenException(user, "You cannot see this submission");
        }

        return submission;
    }

    @GetMapping("/{submissionId}/download")
    private Resource downloadProjectSubmission(@RequestHeader(name = "User") UserDto user,
                                               @PathVariable UUID projectId,
                                               @PathVariable UUID submissionId) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(NotFoundException::new);

        CourseDetailDto course = userCourseService.getUserCourseRoleDetail(project.getCourseId(), user.getId())
                .orElseThrow(NotFoundException::new);

        ProjectSubmission submission = projectSubmissionService.getProjectSubmissionById(submissionId)
                .orElseThrow(NotFoundException::new);

        if (!user.getId().equals(submission.getUserId()) &&
                !course.getRole().hasCollaboratorClearance() &&
                !user.getIsSuperuser()) {
            throw new ForbiddenException(user, "You cannot download this submission");
        }

        return storageService.getProjectSubmissionArchive(submission);
    }

    @GetMapping("/{submissionId}/download/extra")
    private Resource downloadProjectSubmissionExtra(@RequestHeader(name = "User") UserDto user,
                                                    @PathVariable UUID projectId,
                                                    @PathVariable UUID submissionId,
                                                    @RequestParam String filename) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(NotFoundException::new);

        CourseDetailDto course = userCourseService.getUserCourseRoleDetail(project.getCourseId(), user.getId())
                .orElseThrow(NotFoundException::new);

        ProjectSubmission submission = projectSubmissionService.getProjectSubmissionById(submissionId)
                .orElseThrow(NotFoundException::new);

        if (!user.getId().equals(submission.getUserId()) &&
                !course.getRole().hasCollaboratorClearance() &&
                !user.getIsSuperuser()) {
            throw new ForbiddenException(user, "You cannot download this submission");
        }

        return storageService.getProjectSubmissionExtraFile(submission, filename);
    }
}
