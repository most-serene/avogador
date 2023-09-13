package eu.mostserene.avogador.courseservice.courses;

import java.util.Optional;
import java.util.UUID;

public interface CourseService {
    Course createCourse(Course course);
    Course updateCourse(UUID id, Course course);
    Optional<Course> getCourse(UUID id);
    void deleteCourse(UUID courseId);
    Optional<String> getJoinCode(UUID courseId);
    Optional<Course> getByNameAndYear(String name, String year);
}
