package eu.mostserene.avogador.exerciseservice.analytics;

import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/public/analytics")
@Slf4j
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private UserCourseService userCourseService;

    @GetMapping("/users/{userId}/courses/{courseId}/progress")
    private List<StudentTrialStatus> getStudentProgress(@RequestHeader(name = "User") UserDto user,
                                    @PathVariable UUID userId,
                                    @PathVariable UUID courseId) {

        CourseRole courseRole = userCourseService.getUserCourseRole(courseId, user.getId())
                .orElseThrow(NotFoundException::new);

        if (!user.getIsSuperuser() && !courseRole.canSeeHiddenExercises() && !user.getId().equals(userId)) {
            throw new ForbiddenException(user);
        }

        return analyticsService.getStudentProgress(userId, courseId)
                .values().stream().toList();
    }

}
