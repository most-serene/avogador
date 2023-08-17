package eu.mostserene.avogador.courseservice.filesystem;

import java.util.UUID;

public interface FileSystemService {
    Integer createCourse(UUID courseId);
    Integer deleteCourse(UUID courseId);
    Integer archiveCourse(UUID courseId);
}
