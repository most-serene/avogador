package eu.mostserene.avogador.courseservice.storage;

import java.util.UUID;
import java.util.function.BiConsumer;

public interface StorageService {
    void createCourse(UUID courseId);

    Integer deleteCourse(UUID courseId);

    void archiveCourse(UUID courseId, BiConsumer<Boolean, Throwable> handler);
}
