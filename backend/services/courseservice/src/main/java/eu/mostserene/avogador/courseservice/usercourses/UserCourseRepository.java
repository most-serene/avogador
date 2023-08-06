package eu.mostserene.avogador.courseservice.usercourses;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
    List<UserCourse> findByCourse_Id(Long id);
    Optional<UserCourse> findByUserIdAndCourse_Id(Long userId, Long id);
    List<UserCourse> findByUserId(Long userId);

}
