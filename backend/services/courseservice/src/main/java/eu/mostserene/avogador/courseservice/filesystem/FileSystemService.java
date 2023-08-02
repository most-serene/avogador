package eu.mostserene.avogador.courseservice.filesystem;

import eu.mostserene.avogador.courseservice.courses.Course;

public interface FileSystemService {
    Integer createCourse(Long courseId);
    Integer deleteCourse(Long courseId);
    Integer archiveCourse(Long courseId);
}
