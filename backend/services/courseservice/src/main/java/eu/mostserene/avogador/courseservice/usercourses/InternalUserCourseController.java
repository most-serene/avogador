package eu.mostserene.avogador.courseservice.usercourses;

import eu.mostserene.avogador.courseservice.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/courses")
@Slf4j
public class InternalUserCourseController {

    @Autowired
    private UserCourseService userCourseService;

    @GetMapping("{courseId}/users/{userId}")
    private CourseRole getUserCourseByIds(@PathVariable UUID courseId, @PathVariable UUID userId) {
        return userCourseService.getUserCourse(userId, courseId).orElseThrow(NotFoundException::new).getRole();
    }

}
