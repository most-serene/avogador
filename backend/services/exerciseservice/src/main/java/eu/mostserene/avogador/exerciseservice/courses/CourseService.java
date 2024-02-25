package eu.mostserene.avogador.exerciseservice.courses;

import java.util.Optional;
import java.util.UUID;

public interface CourseService {
    Optional<CourseDto> getCourseById(UUID courseId);
}
