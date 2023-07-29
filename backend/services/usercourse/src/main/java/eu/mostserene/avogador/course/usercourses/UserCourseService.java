package eu.mostserene.avogador.course.usercourses;

import eu.mostserene.avogador.course.courses.Course;
import eu.mostserene.avogador.course.users.UserDto;
import org.springframework.stereotype.Service;

@Service
public interface UserCourseService {
    UserCourse getUserCourse(UserDto user, Course course);
    UserCourse createAdmin(UserDto user, Course course);

}
