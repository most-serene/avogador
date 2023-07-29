package eu.mostserene.avogador.course.usercourses;

import eu.mostserene.avogador.course.courses.Course;
import eu.mostserene.avogador.course.users.UserDto;
import eu.mostserene.avogador.course.utils.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCourseServiceImpl implements UserCourseService {
    @Autowired
    private UserCourseRepository repository;

    @Override
    public UserCourse getUserCourse(UserDto user, Course course) {
        return repository
                .findByUserIdAndCourse_Id(user.getId(), course.getId())
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public UserCourse createAdmin(UserDto user, Course course) {
        return repository.save(new UserCourse(user, course, CourseRole.ADMIN));
    }
}
