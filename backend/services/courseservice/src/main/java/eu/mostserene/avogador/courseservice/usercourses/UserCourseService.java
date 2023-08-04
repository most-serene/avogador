package eu.mostserene.avogador.courseservice.usercourses;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.users.UserDto;

import java.util.Optional;

public interface UserCourseService {
    Optional<UserCourse> getUserCourse(UserDto user, Course course);
    Optional<UserCourse> getUserCourse(Long userId, Long courseId);
    UserCourse createAdmin(UserDto user, Course course);
    UserCourse createStudent(UserDto user, Course course);

    UserCourse promoteToCollaborator(UserCourse userCourse);

}
