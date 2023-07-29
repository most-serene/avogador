package eu.mostserene.avogador.courseservice.usercourses;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.users.UserDto;
import eu.mostserene.avogador.courseservice.utils.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserCourseServiceImpl implements UserCourseService {
    @Autowired
    private UserCourseRepository repository;

    @Override
    public Optional<UserCourse> getUserCourse(UserDto user, Course course) {
        return getUserCourse(user.getId(), course.getId());
    }

    @Override
    public Optional<UserCourse> getUserCourse(Long userId, Long courseId) {
        return repository.findByUserIdAndCourse_Id(userId, courseId);
    }

    @Override
    public UserCourse createAdmin(UserDto user, Course course) {
        return repository.save(new UserCourse(user, course, CourseRole.ADMIN));
    }
}
