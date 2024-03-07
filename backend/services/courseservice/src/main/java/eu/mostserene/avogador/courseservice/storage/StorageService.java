package eu.mostserene.avogador.courseservice.storage;

import eu.mostserene.avogador.courseservice.courses.Course;
import org.springframework.core.io.Resource;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

public interface StorageService {
    void createCourse(UUID courseId);

    void deleteCourse(Course course);

    void archiveCourse(UUID courseId, BiConsumer<Boolean, Throwable> handler);

    Optional<Resource> getCourseArchive(UUID courseId);
}
