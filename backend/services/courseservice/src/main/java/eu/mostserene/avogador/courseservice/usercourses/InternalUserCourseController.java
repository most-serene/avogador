package eu.mostserene.avogador.courseservice.usercourses;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.courses.CourseService;
import eu.mostserene.avogador.courseservice.courses.dtos.CourseDetailDto;
import eu.mostserene.avogador.courseservice.utils.LoggerColors;
import eu.mostserene.avogador.courseservice.utils.NotFoundException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/courses")
@Slf4j
public class InternalUserCourseController {

    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private CourseService courseService;

    @GetMapping("/{courseId}/collaborators")
    private UserCourseDtoList getCourseCollaboratorsIds(@PathVariable UUID courseId) {
        Course course = courseService.getCourse(courseId).orElseThrow(NotFoundException::new);

        return new UserCourseDtoList(userCourseService.getUsersByCourseId(courseId)
                .stream()
                .filter(userCourse -> userCourse.getRole().equals(CourseRole.COLLABORATOR) ||
                        userCourse.getRole().equals(CourseRole.ADMIN))
                .map(userCourse -> new UserCourseDto(userCourse.getId(), userCourse.getUser(),
                        userCourse.getCourse().getId(), userCourse.getRole()))
                .peek(userCourse -> log.info(LoggerColors.blue(userCourse.getUserId() + " " + userCourse.getRole())))
                .toList());
    }

    @GetMapping("{courseId}/users/{userId}")
    private CourseDetailDto getUserCourseDetail(@PathVariable UUID courseId, @PathVariable UUID userId) {
        Course course = courseService.getCourse(courseId).orElseThrow(NotFoundException::new);
        Optional<UserCourse> userCourse = userCourseService.getUserCourse(userId, courseId);

        return new CourseDetailDto(course, userCourse
                .map(UserCourse::getRole)
                .orElse(CourseRole.EXTERNAL)
        );
    }

    @Data
    private static class UserCourseDtoList {
        private List<UserCourseDto> userCourses;

        public UserCourseDtoList() {
        }

        public UserCourseDtoList(List<UserCourseDto> userCourses) {
            this.userCourses = userCourses;
        }
    }
}
