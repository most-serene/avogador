package eu.mostserene.avogador.courseservice.courses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl implements CourseService{
    @Autowired
    private CourseRepository repository;

    @Override
    public Course createCourse(Course course) {
        course.setIsArchived(false);
        return repository.save(course);
    }

    @Override
    public Course updateCourse(Long id, Course course) {
        course.setIsArchived(false);
        return repository.save(course);
    }
}
