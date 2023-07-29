package eu.mostserene.avogador.courseservice.usercourses;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.users.UserDto;
import eu.mostserene.avogador.courseservice.utils.NotFoundException;
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
