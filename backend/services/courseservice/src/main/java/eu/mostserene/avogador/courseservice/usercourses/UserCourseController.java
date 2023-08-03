package eu.mostserene.avogador.courseservice.usercourses;


import eu.mostserene.avogador.courseservice.courses.CourseService;
import eu.mostserene.avogador.courseservice.filesystem.FileSystemService;
import eu.mostserene.avogador.courseservice.users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/public/courses/{courseId}")
public class UserCourseController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserCourseService userCourseService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private FileSystemService fileSystemService;

    @PutMapping("/join/{reqJoinCode}")
    private UserCourse joinCourse(HttpServletRequest request, @PathVariable Long courseId, @PathVariable String reqJoinCode){
        var user = userService.getRequestUser(request);
        var course = courseService.getCourse(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course does not exist"));
        var joinCode = courseService.getJoinCode(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));

        if (!reqJoinCode.equals(joinCode)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong join code");
        }
        if (userCourseService.getUserCourse(user.getId(), courseId).isPresent()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are already part of the course");
        }

        return userCourseService.createStudent(user, course);
    }

}
