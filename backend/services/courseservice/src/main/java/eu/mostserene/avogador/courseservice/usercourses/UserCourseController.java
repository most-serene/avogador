package eu.mostserene.avogador.courseservice.usercourses;


import eu.mostserene.avogador.courseservice.courses.ArchivedCourseException;
import eu.mostserene.avogador.courseservice.courses.CourseService;
import eu.mostserene.avogador.courseservice.filesystem.FileSystemService;
import eu.mostserene.avogador.courseservice.users.UserDto;
import eu.mostserene.avogador.courseservice.users.UserService;
import eu.mostserene.avogador.courseservice.utils.LoggerColors;
import eu.mostserene.avogador.courseservice.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/courses")
@Slf4j
public class UserCourseController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserCourseService userCourseService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private FileSystemService fileSystemService;

    /**
     * @param user the request user
     * @param courseId the course to join
     * @param reqJoinCode the provided join code string
     * @return the fresh user-course relation or the already existing one
     * @throws ResponseStatusException(404) if the course does not exist
     * @throws ResponseStatusException(403) if the course is archived or the join code is wrong
     * @throws ResponseStatusException(500) if problems occurred while generating the join code
     * */
    @PutMapping("/{courseId}/join/{reqJoinCode}")
    private UserCourse joinCourse(@RequestHeader(name = "User") UserDto user, @PathVariable UUID courseId, @PathVariable String reqJoinCode){
        var course = courseService.getCourse(courseId)
                .orElseThrow(NotFoundException::new);

        if (course.getIsArchived()){
            throw new ArchivedCourseException();
        }

        var joinCode = courseService.getJoinCode(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));

        if (!reqJoinCode.equals(joinCode)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong join code");
        }

        return userCourseService.getUserCourse(user.getId(), courseId)
                .orElseGet(() -> userCourseService.createStudent(user, course));
    }

    /**
     * @param user the request user
     * @param courseId the id of the course
     * @param userId the id of the user to promote
     * @return the updated user-course relation with role COLLABORATOR
     * @throws ResponseStatusException(403) if the user is not part of the course, is not ADMIN or the course is archived
     * @throws ResponseStatusException(400) if the user to promote is not part of the course
     */
    @PutMapping("/{courseId}/collaborators/{userId}")
    private UserCourse promoteToCollaborator(@RequestHeader(name = "User") UserDto user, @PathVariable UUID courseId, @PathVariable UUID userId){
        var reqUserCourse = userCourseService.getUserCourse(user.getId(), courseId)
                .orElseGet(UserCourse::new);
        if (reqUserCourse.getCourse() == null){
            reqUserCourse.setCourse(
                    courseService.getCourse(courseId)
                            .orElseThrow(() -> new NotFoundException("Course with id " + courseId.toString()))
            );
        }

        if (reqUserCourse.getRole() != CourseRole.ADMIN && !user.getIsSuperuser()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot promote users in this course");
        }
        if (reqUserCourse.getCourse().getIsArchived()){
            throw new ArchivedCourseException();
        }

        var targetUserCourse = userCourseService.getUserCourse(userId, courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not part of the course"));

        return userCourseService.promoteToCollaborator(targetUserCourse);
    }

    /**
     * @param user the request user
     * @param courseId the id of the course
     * @param userId the id of the user to demote
     * @return the updated user-course relation with role STUDENT
     * @throws ResponseStatusException(403) if the user is not part of the course, is not ADMIN or the course is archived
     * @throws ResponseStatusException(400) if the user to promote is not part of the course
     */
    @PutMapping("/{courseId}/students/{userId}")
    private UserCourse demoteToStudent(@RequestHeader(name = "User") UserDto user, @PathVariable UUID courseId, @PathVariable UUID userId){
        var reqUserCourse = userCourseService.getUserCourse(user.getId(), courseId)
                .orElseGet(UserCourse::new);
        if (reqUserCourse.getCourse() == null){
            reqUserCourse.setCourse(
                    courseService.getCourse(courseId)
                            .orElseThrow(() -> new NotFoundException("Course with id " + courseId.toString()))
            );
        }

        if (reqUserCourse.getCourse().getIsArchived()){
            throw new ArchivedCourseException();
        }

        if (reqUserCourse.getRole() != CourseRole.ADMIN && !user.getIsSuperuser()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot demote users in this course");
        }

        var targetUserCourse = userCourseService.getUserCourse(userId, courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not part of the course"));

        return userCourseService.demoteToStudent(targetUserCourse);
    }

    /**
     * @param user the request user
     * @param userId the id of the user
     * @return the courses to which the user belongs
     * @throws ResponseStatusException(400) if the userId doesn't match the requester id
     * */
    @GetMapping("/users/{userId}")
    private List<UserCourse> getCoursesByUser(@RequestHeader(name = "User") UserDto user, @PathVariable UUID userId){
        if (!userId.equals(user.getId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't spy on others!");
        }

        if (!user.getIsSuperuser()) {
            return userCourseService.getCoursesByUserId(userId, false);
        }

        return courseService.getAll().stream()
                .map(course -> userCourseService.getUserCourse(userId, course.getId())
                        .orElseGet(() -> new UserCourse(userId, course, CourseRole.EXTERNAL)))
                .toList();
    }

    /**
     * @param user the request user
     * @param courseId the id of the course
     * @return the users subscribed to the course
     * @throws ResponseStatusException(403) if the user is not part of the course or is a student
     * */
    @GetMapping("/{courseId}/users")
    private List<UserCourseDetailDto> getUsersByCourse(@RequestHeader(name = "User") UserDto user, @PathVariable UUID courseId, @RequestParam Optional<Integer> limit,
                                                       @RequestParam Optional<Integer> offset, @RequestParam Optional<String> orderBy, @RequestParam Optional<String> direction){

        var userCourse = userCourseService.getUserCourse(user.getId(), courseId)
                .orElseGet(UserCourse::new);
        if (userCourse.getRole().getClearance() < CourseRole.COLLABORATOR.getClearance() && !user.getIsSuperuser()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot see the participants of this course");
        }

        int offsetVal = offset.orElse(0);
        var orderByVal = orderBy.orElse("joinDate");

        if (orderByVal.equals("joinDate") || orderByVal.equals("role")){
            String directionVal = direction.orElse("ASC");
            if (!directionVal.equals("ASC") && !directionVal.equals("DESC")){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong order direction");
            }

            return preOrderedGetUsersByCourse(courseId, limit.orElse(0), offsetVal, orderByVal, directionVal);
        }

        List<UserCourse> userCourses = userCourseService.getUsersByCourseId(courseId);
        Map<UUID, UserCourse> userCoursesMap = userCourses.stream().collect(Collectors.toMap(UserCourse::getUser, uc -> uc));

        List<UserDto> userDetails = userService.getUsersFromIdList(userCoursesMap.keySet().stream().toList(), limit, offset, orderBy, direction);

        return userDetails.stream().map(u -> userCoursesMap.get(u.getId()).generateDto(u)).toList();
    }

    private List<UserCourseDetailDto> preOrderedGetUsersByCourse(UUID courseId, Integer limit, Integer offset, String orderBy, String direction){

        Sort sort = Sort.by(direction.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy);

        List<UserCourse> userCourses = userCourseService.getUsersByCourseId(courseId, PageRequest.of(offset, limit == 0 ? Integer.MAX_VALUE : limit, sort));

        List<UserDto> userDetails = userService.getUsersFromIdList(userCourses.stream().map(UserCourse::getUser).toList());
        Map<UUID, UserDto> userDetailsMap = userDetails.stream().collect(Collectors.toMap(UserDto::getId, u -> u));

        return userCourses.stream()
                .map(userCourse -> userCourse.generateDto(userDetailsMap.get(userCourse.getUser())) )
                .toList();
    }

    /**
     * @param user the request user
     * @param courseId the course id
     * @param userId the user to be removed from the course
     * @throws ResponseStatusException(403) if the course is archived, the removed user has greater or equal clearance than the requester
     */
    @DeleteMapping("/{courseId}/users/{userId}")
    private void leaveCourse(@RequestHeader(name = "User") UserDto user, @PathVariable UUID courseId, @PathVariable UUID userId){
        var reqUserCourse = userCourseService.getUserCourse(user.getId(), courseId).
                orElseThrow(NotMemberException::new);

        if (reqUserCourse.getCourse().getIsArchived()){
            throw new ArchivedCourseException();
        }

        if (userId.equals(user.getId()) && reqUserCourse.getRole() == CourseRole.ADMIN){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "An admin cannot remove themselves");
        }
        if (userId.equals(user.getId())){
            userCourseService.removeRelation(reqUserCourse);
            return;
        }

        var targetUserCourse = userCourseService.getUserCourse(userId, courseId)
                .orElseThrow(NotFoundException::new);

        if (targetUserCourse.getRole().getClearance() >= reqUserCourse.getRole().getClearance()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot remove this user");
        }
        userCourseService.removeRelation(targetUserCourse);
    }

}
