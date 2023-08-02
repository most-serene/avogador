package eu.mostserene.avogador.courseservice.courses;

import eu.mostserene.avogador.courseservice.filesystem.FileSystemService;
import eu.mostserene.avogador.courseservice.usercourses.CourseRole;
import eu.mostserene.avogador.courseservice.usercourses.UserCourse;
import eu.mostserene.avogador.courseservice.usercourses.UserCourseService;
import eu.mostserene.avogador.courseservice.users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@RestController
@RequestMapping("/public/courses")
public class CourseController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserCourseService userCourseService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private FileSystemService fileSystemService;


    /**
     * @param request the requests object
     * @param reqCourse the course from the body of the request
     * @return the freshly created course with status code 200
     * @throws ResponseStatusException(403) if the user is not professor
     */
    @PostMapping("")
    private Course createCourse(HttpServletRequest request, @RequestBody Course reqCourse) {
        var user = userService.getRequestUser(request);
        if(!user.getIsProfessor()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot create a course");
        }
        
        var course = courseService.createCourse(reqCourse);
        fileSystemService.createCourse(course.getId());
        userCourseService.createAdmin(user, course);

        return course;
    }

    /**
     * @param request the request object
     * @param courseId the id of the course to update
     * @param reqCourse the updated course from the body of the request
     * @return the updated course
     * @throws ResponseStatusException(400) if courseId and reqCourse.id mismatch
     * @throws ResponseStatusException(403) if the user is not part of the course or has student role
     * */
    @PutMapping("/{courseId}")
    private Course updateCourse(HttpServletRequest request, @PathVariable Long courseId, @RequestBody Course reqCourse){
        if (!Objects.equals(reqCourse.getId(), courseId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course Id mismatch");
        }

        var user = userService.getRequestUser(request);
        var userCourse = userCourseService
                .getUserCourse(user.getId(), courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not part of this course"));
        var course = userCourse.getCourse();

        if (userCourse.getRole() == CourseRole.STUDENT){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot modify this course");
        }
        if (course.getIsArchived()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot modify an archived course");
        }

        return courseService.updateCourse(courseId, reqCourse);
    }

    /**
     * @param request the request object
     * @param courseId the id of the course
     * @return the course corresponding to the id
     * @throws ResponseStatusException(403) if the UserCourse relation does not exist
     * */
    @GetMapping("/{courseId}")
    private UserCourse getCourseById(HttpServletRequest request, @PathVariable Long courseId) { // TODO: this will eventually return more data, such as list of trials
        var user = userService.getRequestUser(request);

        var userCourse =  userCourseService
                .getUserCourse(user.getId(), courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not part of this course or it doesn't exists"));

        if (userCourse.getCourse().getIsArchived() && userCourse.getRole() != CourseRole.ADMIN)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not part of this course or it doesn't exists");

        try{
            String s = courseService.getJoinCode(courseId);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return userCourse;
    }

    /**
     * @param request the request object
     * @param courseId the id of the course to delete
     * @throws ResponseStatusException(403) if the user is not part of the course or doesn't have admin role
     * */
    @DeleteMapping("/{courseId}")
    private void deleteCourseById(HttpServletRequest request, @PathVariable Long courseId){
        var user = userService.getRequestUser(request);
        var userCourse = userCourseService
                .getUserCourse(user.getId(), courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete this course"));

        if (userCourse.getRole() != CourseRole.ADMIN){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete this course");
        }

        fileSystemService.deleteCourse(courseId);
        courseService.deleteCourse(courseId);
    }

    /**
     * @param request the request object
     * @param courseId the id of the course to archive
     * @return the archived course
     * @throws ResponseStatusException(403) if the user is not part of the course or doesn't have admin role
     * */
    @PutMapping("/{courseId}/archive")
    private Course archiveCourseById(HttpServletRequest request, @PathVariable Long courseId){
        var user = userService.getRequestUser(request);
        var userCourse = userCourseService
                .getUserCourse(user.getId(), courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete this course"));
        var course = userCourse.getCourse();

        if (userCourse.getRole() != CourseRole.ADMIN){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete this course");
        }

        course.setIsArchived(true);
        fileSystemService.archiveCourse(courseId);

        return course;
    }


}
