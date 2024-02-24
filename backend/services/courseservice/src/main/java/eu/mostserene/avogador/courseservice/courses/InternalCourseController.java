package eu.mostserene.avogador.courseservice.courses;

import eu.mostserene.avogador.courseservice.usercourses.UserCourseService;
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
public class InternalCourseController {

    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private CourseService courseService;

    @GetMapping("/{courseId}")
    private Course getCourseById(@PathVariable UUID courseId) {
        return courseService.getCourse(courseId).orElseThrow(NotFoundException::new);
    }

}
