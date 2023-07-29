package eu.mostserene.avogador.courseservice.courses;

public interface CourseService {
    Course createCourse(Course course);
    Course updateCourse(Long id, Course course);
}
