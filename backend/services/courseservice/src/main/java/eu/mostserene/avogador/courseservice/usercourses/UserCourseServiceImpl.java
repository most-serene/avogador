package eu.mostserene.avogador.courseservice.usercourses;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.users.UserDto;
import eu.mostserene.avogador.courseservice.utils.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Override
    public UserCourse createStudent(UserDto user, Course course) {
        return repository.save(new UserCourse(user, course, CourseRole.STUDENT));
    }

    @Override
    public UserCourse promoteToCollaborator(UserCourse userCourse) {
        userCourse.setRole(CourseRole.COLLABORATOR);
        return userCourse;
    }

    @Override
    public UserCourse demoteToStudent(UserCourse userCourse) {
        userCourse.setRole(CourseRole.STUDENT);
        return userCourse;
    }

    @Override
    public List<UserCourse> getCoursesByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public List<UserCourse> getUsersByCourseId(Long courseId) {
        return repository.findByCourse_Id(courseId);
    }

    @Override
    public void removeRealation(UserCourse userCourse) {
        repository.delete(userCourse);
    }
}
