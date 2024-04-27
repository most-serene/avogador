package eu.mostserene.avogador.exerciseservice.projectservice.userproject;

import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.projectservice.projects.Project;
import eu.mostserene.avogador.exerciseservice.projectservice.projects.ProjectService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.users.UserService;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/public/projects/{projectId}/users")
public class UserProjectController {
    @Autowired
    private UserProjectService userProjectService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCourseService userCourseService;

    @GetMapping("/{userId}")
    private UserProject getUserProject(@RequestHeader(name = "User") UserDto user, @PathVariable UUID projectId, @PathVariable UUID userId) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(NotFoundException::new);
        CourseRole userRole = userCourseService.getUserCourseRoleDetail(project.getCourseId(), userId)
                .orElseThrow(() -> new ForbiddenException(user)).getRole();

        if (!user.getId().equals(userId) || (userRole.getClearance() <= CourseRole.EXTERNAL.getClearance() && !user.getIsSuperuser())) {
            throw new ForbiddenException(user);
        }

        if (user.getIsSuperuser() || userRole.getClearance() >= CourseRole.COLLABORATOR.getClearance()) {
            return null;
        }

        return userProjectService.getUserProject(project, user)
                .orElseThrow(() -> new NotFoundException("UserProject not found"));
    }

    @PutMapping("/marks")
    private void uploadMarks(@RequestHeader(name = "User") UserDto user,
                             @PathVariable UUID projectId, @RequestBody List<Pair<UUID, Integer>> marks) {
        throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "Not yet supported");
    }

}
