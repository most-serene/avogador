package eu.mostserene.avogador.course.usercourses;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
    Optional<UserCourse> findByUserIdAndCourse_Id(Long userId, Long id);
}
