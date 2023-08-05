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

        return userCourseService.getUserCourse(user.getId(), courseId)
                .orElse(userCourseService.createStudent(user, course));
    }

    @PutMapping("/collaborators/{userId}")
    private UserCourse promoteToCollaborator(HttpServletRequest request, @PathVariable Long courseId, @PathVariable Long userId){
        var user = userService.getRequestUser(request);
        var reqUserCourse = userCourseService.getUserCourse(user.getId(), courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot promote users in this course"));

        if (reqUserCourse.getRole() != CourseRole.ADMIN){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot promote users in this course");
        }

        var targetUserCourse = userCourseService.getUserCourse(userId, courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not part of the course"));

        return userCourseService.promoteToCollaborator(targetUserCourse);
    }

    @PutMapping("/students/{userId}")
    private UserCourse demoteToStudent(HttpServletRequest request, @PathVariable Long courseId, @PathVariable Long userId){
        var user = userService.getRequestUser(request);
        var reqUserCourse = userCourseService.getUserCourse(user.getId(), courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot demote users in this course"));

        if (reqUserCourse.getRole() != CourseRole.ADMIN){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot demote users in this course");
        }

        var targetUserCourse = userCourseService.getUserCourse(userId, courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not part of the course"));

        return userCourseService.demoteToStudent(targetUserCourse);
    }

}
