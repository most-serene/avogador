package eu.mostserene.avogador.usercourse.usercourses;

import eu.mostserene.avogador.usercourse.courses.Course;
import eu.mostserene.avogador.usercourse.users.UserDto;
import eu.mostserene.avogador.usercourse.utils.NotFoundException;
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
