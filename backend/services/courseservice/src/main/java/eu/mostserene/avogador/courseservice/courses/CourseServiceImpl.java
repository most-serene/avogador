package eu.mostserene.avogador.courseservice.courses;

import eu.mostserene.avogador.courseservice.utils.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    @Override
    public Optional<Course> getCourse(Long id) {
        return repository.findById(id);
    }
}
