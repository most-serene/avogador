package eu.mostserene.avogador.exerciseservice.projectservice.notebookprojects;

import eu.mostserene.avogador.exerciseservice.courses.CourseDetailDto;
import eu.mostserene.avogador.exerciseservice.courses.CourseService;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.projectservice.projects.ProjectService;
import eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions.ProjectSubmission;
import eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions.ProjectSubmissionService;
import eu.mostserene.avogador.exerciseservice.projectservice.userproject.UserProjectService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/public/projects/notebook/{projectId}")
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

    @PostMapping("/submissions")
    private ProjectSubmission createNotebookProjectSubmission(@RequestHeader(name = "User") UserDto user,
                                                              @PathVariable UUID projectId,
                                                              @RequestBody MultipartFile project) {
        NotebookProject notebookProject = notebookProjectService.getNotebookProject(projectId)
                .orElseThrow(NotFoundException::new);

        CourseDetailDto courseDetail = userCourseService.getUserCourseRoleDetail(notebookProject.getCourseId(), user.getId())
                .orElseThrow(NotFoundException::new);

        if (courseDetail.getIsArchived()) {
            throw new ResponseStatusException(HttpStatus.GONE, "This course has been archived");
        }

        if (userProjectService.getUserProject(projectId, user.getId()).isEmpty() &&
                !user.getIsSuperuser() &&
                !courseDetail.getRole().hasCollaboratorClearance()
        ) {
            throw new ForbiddenException("You have not joined the project");
        }

        if (!notebookProject.getCanSubmit()) {
            throw new ForbiddenException("Submission are currently forbidden");
        }

        return projectSubmissionService.createSubmission(notebookProject, user, project);
    }

}
