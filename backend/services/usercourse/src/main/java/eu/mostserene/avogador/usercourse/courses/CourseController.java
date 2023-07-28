package eu.mostserene.avogador.usercourse.courses;

import eu.mostserene.avogador.usercourse.usercourses.UserCourseService;
import eu.mostserene.avogador.usercourse.users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/public/courses")
public class CourseController {
    private final CourseRepository repository;

    @Autowired
    private UserService userService;
    @Autowired
    private UserCourseService userCourseService;


    CourseController(CourseRepository rep) {
        this.repository = rep;
    }

    @PostMapping("")
    public Course createCourse(HttpServletRequest request, @RequestBody Course _course) {
        var user = userService.getRequestUser(request);
        System.out.println("EEEAAAAA");
        if(!user.getIsProfessor())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You cannot create a test");
        System.out.println("UHUHUH");

        var course = repository.save(_course);
        userCourseService.createAdmin(user, course);
        return course;
    }


}
