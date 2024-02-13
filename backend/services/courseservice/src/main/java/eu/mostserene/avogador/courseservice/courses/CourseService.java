package eu.mostserene.avogador.courseservice.courses;

import eu.mostserene.avogador.courseservice.usercourses.UserCourse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseService {
    Course createCourse(Course course);

    Course updateCourse(UUID id, Course course);

    Optional<Course> getCourse(UUID id);

    List<Course> getAll();

    void deleteCourse(UUID courseId);

    Course archiveCourse(UserCourse course);

    Optional<String> getJoinCode(UUID courseId);

    Optional<Course> getByNameAndYear(String name, String year);
}
