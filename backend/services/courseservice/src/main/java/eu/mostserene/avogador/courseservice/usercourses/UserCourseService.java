package eu.mostserene.avogador.courseservice.usercourses;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.users.UserDto;

public interface UserCourseService {
    UserCourse getUserCourse(UserDto user, Course course);
    UserCourse getUserCourse(Long userId, Long courseId);

    UserCourse createAdmin(UserDto user, Course course);

}
