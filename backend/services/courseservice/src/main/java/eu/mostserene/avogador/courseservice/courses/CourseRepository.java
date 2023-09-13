package eu.mostserene.avogador.courseservice.courses;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    Optional<Course> findByNameAndYear(String name, String year);

}
