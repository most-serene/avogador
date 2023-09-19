package eu.mostserene.avogador.exerciseservice.courses;

import java.util.Optional;
import java.util.UUID;

public interface UserCourseService {

    Optional<CourseRole> getUserCourseRole(UUID courseId, UUID userId);

}
