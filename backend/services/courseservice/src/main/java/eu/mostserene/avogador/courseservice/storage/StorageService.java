package eu.mostserene.avogador.courseservice.storage;

import org.springframework.core.io.Resource;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

public interface StorageService {
    void createCourse(UUID courseId);

    Integer deleteCourse(UUID courseId);

    void archiveCourse(UUID courseId, BiConsumer<Boolean, Throwable> handler);

    Optional<Resource> getCourseArchive(UUID courseId);
}
