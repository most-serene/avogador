package eu.mostserene.avogador.courseservice.courses;

import eu.mostserene.avogador.courseservice.courses.dtos.CourseDetailDto;
import eu.mostserene.avogador.courseservice.storage.StorageService;
import eu.mostserene.avogador.courseservice.usercourses.CourseRole;
import eu.mostserene.avogador.courseservice.usercourses.UserCourseService;
import eu.mostserene.avogador.courseservice.users.UserDto;
import eu.mostserene.avogador.courseservice.users.UserService;
import eu.mostserene.avogador.courseservice.utils.BadRequestException;
import eu.mostserene.avogador.courseservice.utils.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.UUID;

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
    private StorageService storageService;


    /**
     * @param user      the request user
     * @param reqCourse the course from the body of the request
     * @return the freshly created course with status code 200
     * @throws ResponseStatusException(403) if the user is not professor
     */
    @PostMapping("")
    private Course createCourse(@RequestHeader(name = "User") UserDto user, @RequestBody Course reqCourse) {
        if (!user.getIsProfessor()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot create a course");
        }

        if (courseService.getByNameAndYear(reqCourse.getName(), reqCourse.getYear()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already existing course");
        }

        var course = courseService.createCourse(reqCourse);
        storageService.createCourse(course.getId());
        userCourseService.createAdmin(user, course);

        return course;
    }

    /**
     * @param user      the request user
     * @param courseId  the id of the course to update
     * @param reqCourse the updated course from the body of the request
     * @return the updated course
     * @throws ResponseStatusException(400) if courseId and reqCourse.id mismatch
     * @throws ResponseStatusException(403) if the user is not part of the course or has student role
     * @throws ResponseStatusException(403) if the course is archived
     */
    @PutMapping("/{courseId}")
    private Course updateCourse(@RequestHeader(name = "User") UserDto user, @PathVariable UUID courseId, @RequestBody Course reqCourse) {
        if (!Objects.equals(reqCourse.getId(), courseId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course Id mismatch");
        }

        var userCourse = userCourseService
                .getUserCourse(user.getId(), courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not part of this course"));
        var course = userCourse.getCourse();

        if (userCourse.getRole() == CourseRole.STUDENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot modify this course");
        }
        if (course.getIsArchived()) {
            throw new ArchivedCourseException();
        }

        return courseService.updateCourse(courseId, reqCourse);
    }

    /**
     * @param user     the request user
     * @param courseId the id of the course
     * @return the course corresponding to the id together with the joinCode
     * @throws ResponseStatusException(403) if the UserCourse relation does not exist
     * @throws ResponseStatusException(500) if the CourseService couldn't create a join code
     */
    @GetMapping("/{courseId}")
    private CourseDetailDto getCourseById(@RequestHeader(name = "User") UserDto user, @PathVariable UUID courseId) { // TODO: this will eventually return more data, such as list of trials
        var course = courseService.getCourse(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "This course doesn't exists"));

        var userCourse = userCourseService
                .getUserCourse(user.getId(), courseId);
        if (userCourse.isEmpty() && !course.getIsArchived())
            return new CourseDetailDto(course, CourseRole.EXTERNAL);
        var userRole = userCourse
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not part of this course or it doesn't exists"))
                .getRole();

        if (course.getIsArchived() && userRole.getClearance() < CourseRole.STUDENT.getClearance()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not part of this course or it doesn't exists");
        }

        if (course.getIsArchived() && userRole == CourseRole.STUDENT) {
            throw new ArchivedCourseException();
        }

        if (userRole == CourseRole.STUDENT) {
            return new CourseDetailDto(course, userRole);
        }
        var joinCode = courseService.getJoinCode(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));

        return new CourseDetailDto(course, joinCode, userRole);

    }

    /**
     * @param user     the request user
     * @param courseId the id of the course to delete
     * @throws ResponseStatusException(403) if the user is not part of the course or doesn't have admin role
     */
    @DeleteMapping("/{courseId}")
    private void deleteCourseById(@RequestHeader(name = "User") UserDto user, @PathVariable UUID courseId) {
        var userCourse = userCourseService
                .getUserCourse(user.getId(), courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete this course"));

        if (userCourse.getRole() != CourseRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete this course");
        }

        storageService.deleteCourse(courseId);
        courseService.deleteCourse(courseId);
    }

    /**
     * @param user     the request user
     * @param courseId the id of the course to archive
     * @return the course archive
     * @throws ResponseStatusException(403) if the user is not part of the course or doesn't have admin role
     */
    @GetMapping("/{courseId}/archive")
    private ResponseEntity<Resource> getArchiveCourseById(@RequestHeader(name = "User") UserDto user, @PathVariable UUID courseId) {
        var userCourse = userCourseService
                .getUserCourse(user.getId(), courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot get the archive of this course"));

        if (userCourse.getRole().getClearance() < CourseRole.COLLABORATOR.getClearance() && !user.getIsSuperuser()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot get the archive of this course");
        }

        if (!userCourse.getCourse().getIsArchived()) {
            throw new BadRequestException("The course is not archived");
        }

        Course course = userCourse.getCourse();
        Resource courseArchive = storageService.getCourseArchive(course.getId())
                .orElseThrow(() -> new NotFoundException("Course - " + course.getId() + ": archive not found"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + course.getName() + ".tar.gz\"")
                .body(courseArchive);
    }

    /**
     * @param user     the request user
     * @param courseId the id of the course to archive
     * @return the archived course
     * @throws ResponseStatusException(403) if the user is not part of the course or doesn't have admin role
     */
    @PutMapping("/{courseId}/archive")
    private Course archiveCourseById(@RequestHeader(name = "User") UserDto user, @PathVariable UUID courseId) {
        var userCourse = userCourseService
                .getUserCourse(user.getId(), courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot archive this course"));

        if (userCourse.getRole() != CourseRole.ADMIN && !user.getIsSuperuser()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot archive this course");
        }

        userCourse.getCourse().requireNotArchived();

        try {
            return courseService.archiveCourse(userCourse);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

}
