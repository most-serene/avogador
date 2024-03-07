package eu.mostserene.avogador.courseservice.usercourses;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCourseRepository extends JpaRepository<UserCourse, UUID> {
    List<UserCourse> findByUserIdAndCourse_IsArchived(UUID userId, Boolean isArchived);

    List<UserCourse> findByCourse_Id(UUID id);

    List<UserCourse> findByCourse_Id(UUID id, Pageable pageable);

    Optional<UserCourse> findByUserIdAndCourse_Id(UUID userId, UUID id);

    List<UserCourse> findByUserId(UUID userId);

    void deleteByCourse_Id(UUID courseId);
}
