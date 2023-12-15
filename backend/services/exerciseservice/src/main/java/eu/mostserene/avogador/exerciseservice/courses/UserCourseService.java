package eu.mostserene.avogador.exerciseservice.courses;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCourseService {
    List<UserCourseDto> getCourseCollaborators(UUID courseId);
    Optional<CourseRole> getUserCourseRole(UUID courseId, UUID userId);
}
