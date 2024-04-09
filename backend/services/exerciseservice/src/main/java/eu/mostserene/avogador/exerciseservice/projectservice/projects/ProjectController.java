package eu.mostserene.avogador.exerciseservice.projectservice.projects;

import eu.mostserene.avogador.exerciseservice.courses.CourseDetailDto;
import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/public/projects")
public class ProjectController {
    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/{projectId}")
    private Project getProjectById(@RequestHeader(name = "User") UserDto user,
                                   @PathVariable UUID projectId) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(NotFoundException::new);

        CourseDetailDto course = userCourseService.getUserCourseRoleDetail(project.getCourseId(), user.getId())
                .orElseThrow(NotFoundException::new);

        if (course.getRole().getClearance() < CourseRole.STUDENT.getClearance() && !user.getIsSuperuser()) {
            throw new ForbiddenException(user, "You cannot see this project");
        }

        return project;
    }
}
