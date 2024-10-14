package eu.mostserene.avogador.exerciseservice.projectservice.projects;

import eu.mostserene.avogador.exerciseservice.courses.CourseDetailDto;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.projectservice.userproject.UserProject;
import eu.mostserene.avogador.exerciseservice.projectservice.userproject.UserProjectService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/public/projects")
public class ProjectController {
    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserProjectService userProjectService;

    @GetMapping("/{projectId}")
    private Project getProjectById(@RequestHeader(name = "User") UserDto user,
                                   @PathVariable UUID projectId) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(NotFoundException::new);

        CourseDetailDto course = userCourseService.getCourseMember(project.getCourseId(), user)
                .orElseThrow(NotFoundException::new);

        return project;
    }

    @PutMapping("/{projectId}/join")
    private UserProject joinProject(@RequestHeader(name = "User") UserDto user,
                                    @PathVariable UUID projectId) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(NotFoundException::new);

        CourseDetailDto course = userCourseService.getCourseMember(project.getCourseId(), user)
                .orElseThrow(NotFoundException::new)
                .requireNotArchived();

        if (user.getIsSuperuser() || course.getRole().hasCollaboratorClearance()) {
            return null;
        }

        /*
        if (new Date().after(project.getDeadline())) {
            throw new BadRequestException("This Project is ended");
        }
         */

        return userProjectService.joinProject(user, project);
    }

    @GetMapping("/courses/{courseId}")
    private List<Project> getProjectsByCourse(@RequestHeader(name = "User") UserDto user,
                                              @PathVariable UUID courseId) {
        userCourseService.getCourseMember(courseId, user)
                .orElseThrow(() -> new ForbiddenException(user));

        return projectService.getProjectsByCourseId(courseId);
    }
}
