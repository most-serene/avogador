package eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions;

import eu.mostserene.avogador.exerciseservice.courses.CourseDetailDto;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.projectservice.projects.Project;
import eu.mostserene.avogador.exerciseservice.projectservice.projects.ProjectService;
import eu.mostserene.avogador.exerciseservice.projectservice.userproject.UserProject;
import eu.mostserene.avogador.exerciseservice.projectservice.userproject.UserProjectService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.utils.BadRequestException;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/public/projects/{projectId}/submissions")
public class ProjectSubmissionController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectSubmissionService projectSubmissionService;

    @Autowired
    private UserProjectService userProjectService;

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
        return storageService.getProjectSubmissionArchive(
                getProjectSubmissionIfPermitted(user, projectId, submissionId)
        );
    }

    @GetMapping("/{submissionId}/download/extra")
    private Resource downloadProjectSubmissionExtra(@RequestHeader(name = "User") UserDto user,
                                                    @PathVariable UUID projectId,
                                                    @PathVariable UUID submissionId,
                                                    @RequestParam String filename) {
        return storageService.getProjectSubmissionExtraFile(
                        getProjectSubmissionIfPermitted(user, projectId, submissionId),
                        filename)
                .orElseThrow(NotFoundException::new);
    }

    @PutMapping("/{submissionId}/confirm")
    private ProjectSubmission confirmProjectSubmission(@RequestHeader(name = "User") UserDto user,
                                                       @PathVariable UUID projectId,
                                                       @PathVariable UUID submissionId) {
        ProjectSubmission submission = projectSubmissionService.getProjectSubmissionById(submissionId)
                .orElseThrow(NotFoundException::new);

        if (!ProjectStatus.SUCCESS.equals(submission.getStatus())) {
            throw new BadRequestException("A ProjectSubmission can be confirmed only if its status is SUCCESS");
        }

        if (!user.getId().equals(submission.getUserId())) {
            throw new ForbiddenException(user, "You cannot see this submission");
        }

        return projectSubmissionService.confirmSubmission(submission);
    }

    private ProjectSubmission getProjectSubmissionIfPermitted(UserDto user, UUID projectId, UUID submissionId) {
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

        return submission;
    }

    @GetMapping("")
    private List<ProjectSubmission> getUsersLastProjectSubmission(@RequestHeader(name = "User") UserDto user,
                                                                  @PathVariable UUID projectId) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(NotFoundException::new);

        CourseDetailDto course = userCourseService.getUserCourseRoleDetail(project.getCourseId(), user.getId())
                .orElseThrow(NotFoundException::new);

        if (!course.getRole().hasCollaboratorClearance() && !user.getIsSuperuser()) {
            throw new ForbiddenException(user);
        }

        List<UUID> projectUsers = userProjectService.getUsersFromProject(project)
                .stream().map(UserProject::getUserId).toList();

        return projectSubmissionService.getUsersLastProjectSubmissions(project, projectUsers);
    }

    @GetMapping("/users/{userId}")
    private List<ProjectSubmission> getUserProjectSubmissions(@RequestHeader(name = "User") UserDto user,
                                                              @PathVariable UUID projectId, @PathVariable UUID userId,
                                                              @RequestParam(defaultValue = "false") boolean latest) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(NotFoundException::new);

        CourseDetailDto course = userCourseService.getUserCourseRoleDetail(project.getCourseId(), user.getId())
                .orElseThrow(NotFoundException::new);

        List<ProjectSubmission> submissions = latest ?
                projectSubmissionService.getLatestUserSubmissions(project, userId) :
                projectSubmissionService.getUserSubmissions(project, userId);

        if (submissions.isEmpty()) {
            return List.of();
        }

        if (!user.getId().equals(submissions.get(0).getUserId()) &&
                !course.getRole().hasCollaboratorClearance() &&
                !user.getIsSuperuser()) {
            throw new ForbiddenException(user, "You cannot see this submission");
        }

        return submissions;
    }
}
