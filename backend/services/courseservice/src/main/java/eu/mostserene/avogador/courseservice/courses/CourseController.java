package eu.mostserene.avogador.courseservice.courses;

import eu.mostserene.avogador.courseservice.usercourses.CourseRole;
import eu.mostserene.avogador.courseservice.usercourses.UserCourse;
import eu.mostserene.avogador.courseservice.usercourses.UserCourseService;
import eu.mostserene.avogador.courseservice.users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/public/courses")
public class CourseController {
    private final CourseRepository repository;

    @Autowired
    private UserService userService;
    @Autowired
    private UserCourseService userCourseService;
    @Autowired
    private CourseService courseService;

    CourseController(CourseRepository rep) {
        this.repository = rep;
    }

    @PostMapping("")
    public Course createCourse(HttpServletRequest request, @RequestBody Course reqCourse) {
        var user = userService.getRequestUser(request);
        if(!user.getIsProfessor()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You cannot create a course");
        }

        var course = courseService.createCourse(reqCourse);
        userCourseService.createAdmin(user, course);

        return course;
    }

    @PutMapping("/{courseId}")
    public Course updateCourse(HttpServletRequest request, @PathVariable Long courseId, @RequestBody Course reqCourse){
        var user = userService.getRequestUser(request);
        var userCourse = userCourseService
                .getUserCourse(user.getId(), courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not part of this course"));

        if (userCourse.getRole() == CourseRole.STUDENT){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You cannot modify this course");
        }

        return courseService.updateCourse(courseId, reqCourse);
    }

    @GetMapping("/{courseId}")
    public UserCourse getCourseById(HttpServletRequest request, @PathVariable Long courseId) {
        var user = userService.getRequestUser(request);

        return userCourseService
                .getUserCourse(user.getId(), courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not part of this course"));
    }


}
