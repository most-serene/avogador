package eu.mostserene.avogador.courseservice.storage;

import java.util.UUID;

public interface StorageService {
    void createCourse(UUID courseId);
    Integer deleteCourse(UUID courseId);
    Integer archiveCourse(UUID courseId);
}
