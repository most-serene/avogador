package eu.mostserene.avogador.exerciseservice.courses;

import eu.mostserene.avogador.exerciseservice.users.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCourseService {
    List<UserCourseDto> getCourseCollaborators(UUID courseId);

    @Deprecated
    Optional<CourseDetailDto> getUserCourseRoleDetail(UUID courseId, UUID userId);

    Optional<CourseDetailDto> getCourseMember(UUID courseId, UserDto user);

    Optional<CourseDetailDto> getCourseCollaborator(UUID courseId, UserDto user);

    Optional<CourseDetailDto> getCourseAdmin(UUID courseId, UserDto user);


}
