package eu.mostserene.avogador.exerciseservice.projectservice.userproject;

import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.projectservice.projects.Project;
import eu.mostserene.avogador.exerciseservice.projectservice.projects.ProjectService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.users.UserService;
import eu.mostserene.avogador.exerciseservice.utils.BadRequestException;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @GetMapping("/self")
    private UserProject getUserProject(@RequestHeader(name = "User") UserDto user, @PathVariable UUID projectId) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(NotFoundException::new);
        CourseRole userRole = userCourseService.getCourseMember(project.getCourseId(), user)
                .orElseThrow(() -> new ForbiddenException(user)).getRole();

        if (userRole.hasCollaboratorClearance() || user.getIsSuperuser()) {
            return null;
        }

        return userProjectService.getUserProject(project, user)
                .orElseThrow(() -> new NotFoundException("UserProject not found"));
    }

    @GetMapping("")
    private List<UserProjectDto> getUsersFromProject(@RequestHeader(name = "User") UserDto user, @PathVariable UUID projectId) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(NotFoundException::new);
        userCourseService.getCourseCollaborator(project.getCourseId(), user)
                .orElseThrow(() -> new ForbiddenException(user));

        List<UserProject> userProjects = userProjectService.getUsersFromProject(project);
        if (userProjects.isEmpty()) {
            return List.of();
        }

        Map<UUID, UserDto> users = userService.getUsersFromIdList(userProjects.stream().map(UserProject::getUserId).toList()).
                stream().collect(Collectors.toMap(UserDto::getId, u -> u));

        return userProjects.stream()
                .map(userProject -> userProject.getUserProjectDetail(users.get(userProject.getUserId())))
                .toList();
    }

    @PutMapping("/marks")
    private List<UserProject> uploadMarks(@RequestHeader(name = "User") UserDto user,
                                          @PathVariable UUID projectId, @RequestBody Map<String, Integer> marks) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(NotFoundException::new);

        userCourseService.getCourseCollaborator(project.getCourseId(), user)
                .orElseThrow(() -> new ForbiddenException(user))
                .requireNotArchived();

        validateMarks(marks);

        Map<UserDto, Integer> userMarkMap = new HashMap<>();
        try {
            userService.getOrCreateUsers(marks.keySet().stream().toList())
                    .forEach(userDto -> userMarkMap.put(userDto, marks.get(userDto.getEmail())));
        } catch (HttpClientErrorException.BadRequest e) {
            throw new BadRequestException("Some emails domains are invalid");
        }

        return userMarkMap.entrySet().stream().map(
                mark -> userProjectService.uploadMark(mark.getKey(), project, mark.getValue())
        ).toList();
    }

    private void validateMarks(Map<String, Integer> marks) {
        if (marks.values().parallelStream().anyMatch(mark -> mark < 0 || mark > 31)) {
            throw new BadRequestException("Some marks are invalid: less than 0 or more than 31");
        }
    }

}
