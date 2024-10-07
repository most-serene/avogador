package eu.mostserene.avogador.exerciseservice.projectservice.notebookprojects;

import eu.mostserene.avogador.exerciseservice.courses.CourseDetailDto;
import eu.mostserene.avogador.exerciseservice.courses.CourseService;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.projectservice.projects.Project;
import eu.mostserene.avogador.exerciseservice.projectservice.projects.ProjectService;
import eu.mostserene.avogador.exerciseservice.projectservice.projects.ProjectType;
import eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions.ProjectStatus;
import eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions.ProjectSubmission;
import eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions.ProjectSubmissionService;
import eu.mostserene.avogador.exerciseservice.projectservice.userproject.UserProjectService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.utils.BadRequestException;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/public/projects/notebook")
public class NotebookProjectController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private NotebookProjectService notebookProjectService;

    @Autowired
    private UserProjectService userProjectService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private ProjectSubmissionService projectSubmissionService;

    @PostMapping("")
    private Project createNotebookProject(@RequestHeader(name = "User") UserDto user, @RequestBody NotebookProject project) {
        userCourseService.getCourseCollaborator(project.getCourseId(), user)
                .orElseThrow(NotFoundException::new)
                .requireNotArchived();

        return notebookProjectService.createProject(project);
    }

    @PutMapping("/{projectId}")
    private Project updateNotebookProject(@RequestHeader(name = "User") UserDto user, @PathVariable UUID projectId, @RequestBody NotebookProject project) {
        Project storedProject = projectService.getProjectById(projectId)
                .orElseThrow(NotFoundException::new);

        if (!storedProject.getProjectType().equals(ProjectType.NOTEBOOK)) {
            throw new BadRequestException("Project is not a notebook project");
        }

        if (!storedProject.getId().equals(project.getId())) {
            throw new BadRequestException("Project ID mismatch");
        }

        CourseDetailDto course = userCourseService.getCourseCollaborator(storedProject.getCourseId(), user)
                .orElseThrow(NotFoundException::new)
                .requireNotArchived();

        storedProject.setName(project.getName());
        storedProject.setDescription(project.getDescription());
        storedProject.setCanSubmit(project.getCanSubmit());
        storedProject.setDeadline(project.getDeadline());

        return notebookProjectService.updateProject(project);
    }

    @PostMapping("/{projectId}/submissions")
    private ProjectSubmission createNotebookProjectSubmission(@RequestHeader(name = "User") UserDto user,
                                                              @PathVariable UUID projectId,
                                                              @RequestBody MultipartFile project) {
        NotebookProject notebookProject = notebookProjectService.getNotebookProject(projectId)
                .orElseThrow(NotFoundException::new);

        CourseDetailDto courseDetail = userCourseService.getCourseMember(notebookProject.getCourseId(), user)
                .orElseThrow(NotFoundException::new)
                .requireNotArchived();

        if (userProjectService.getUserProject(notebookProject, user).isEmpty() &&
                !user.getIsSuperuser() &&
                !courseDetail.getRole().hasCollaboratorClearance()
        ) {
            throw new ForbiddenException(user, "You have not joined the project");
        }

        if (!notebookProject.getCanSubmit()) {
            throw new ForbiddenException(user, "Submission are currently forbidden");
        }

        List<ProjectSubmission> submissionsList = projectSubmissionService.getUserSubmissions(notebookProject, user.getId());

        if (submissionsList.stream().anyMatch(submission -> submission.getStatus().equals(ProjectStatus.PENDING))) {
            throw new ForbiddenException(user, "You have another submission pending for judgment");
        }

        if (submissionsList.stream().anyMatch(submission -> submission.getStatus().equals(ProjectStatus.CONFIRMED))) {
            throw new ForbiddenException(user, "You have another confirmed a submission");
        }

        return projectSubmissionService.createSubmission(notebookProject, user, project);
    }

}
