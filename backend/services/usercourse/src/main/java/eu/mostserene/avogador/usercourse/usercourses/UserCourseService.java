package eu.mostserene.avogador.usercourse.usercourses;

import eu.mostserene.avogador.usercourse.courses.Course;
import eu.mostserene.avogador.usercourse.users.UserDto;
import org.springframework.stereotype.Service;

@Service
public interface UserCourseService {
    UserCourse getUserCourse(UserDto user, Course course);
    UserCourse createAdmin(UserDto user, Course course);

}
