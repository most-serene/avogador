package eu.mostserene.avogador.courseservice.courses;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public interface CourseService {
    Course createCourse(Course course);
    Course updateCourse(Long id, Course course);
    Optional<Course> getCourse(Long id);
    void deleteCourse(Long courseId);
    String getJoinCode(Long courseId) throws NoSuchAlgorithmException, InvalidKeyException;
}
