package eu.mostserene.avogador.courseservice.usercourses;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.users.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCourseService {
    Optional<UserCourse> getUserCourse(UUID userId, UUID courseId);

    UserCourse createAdmin(UserDto user, Course course);

    UserCourse createStudent(UserDto user, Course course);

    UserCourse promoteToCollaborator(UserCourse userCourse);

    UserCourse demoteToStudent(UserCourse userCourse);

    List<UserCourse> getCoursesByUserId(UUID userId, Boolean isArchived);

    List<UserCourse> getUsersByCourseId(UUID courseId);

    List<UserCourse> getUsersByCourseId(UUID courseId, Pageable pageable);

    void removeRelation(UserCourse userCourse);

    void deleteByCourse(Course course);
}
