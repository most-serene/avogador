package eu.mostserene.avogador.exerciseservice.courses;

import java.util.Optional;
import java.util.UUID;

public interface UserCourseService {

    Optional<CourseRole> getUserCourse(UUID courseId, UUID userId);

}
