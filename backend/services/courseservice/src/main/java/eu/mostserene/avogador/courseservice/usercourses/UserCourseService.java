package eu.mostserene.avogador.courseservice.usercourses;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.users.UserDto;
import org.springframework.stereotype.Service;

@Service
public interface UserCourseService {
    UserCourse getUserCourse(UserDto user, Course course);
    UserCourse createAdmin(UserDto user, Course course);

}
