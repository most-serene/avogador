package eu.mostserene.avogador.courseservice.usercourses;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.users.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserCourseServiceImpl implements UserCourseService {
    @Autowired
    private UserCourseRepository repository;

    @Override
    public Optional<UserCourse> getUserCourse(UUID userId, UUID courseId) {
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
        return repository.save(userCourse);
    }

    @Override
    public UserCourse demoteToStudent(UserCourse userCourse) {
        userCourse.setRole(CourseRole.STUDENT);
        return repository.save(userCourse);
    }

    @Override
    public List<UserCourse> getCoursesByUserId(UUID userId, Boolean isArchived) {
        return repository.findByUserIdAndCourse_IsArchived(userId, isArchived);
    }

    @Override
    public List<UserCourse> getUsersByCourseId(UUID courseId) {
        return repository.findByCourse_Id(courseId);
    }

    @Override
    public List<UserCourse> getUsersByCourseId(UUID courseId, Pageable pageable) {
        return repository.findByCourse_Id(courseId, pageable);
    }

    @Override
    public void removeRelation(UserCourse userCourse) {
        repository.delete(userCourse);
    }
}
