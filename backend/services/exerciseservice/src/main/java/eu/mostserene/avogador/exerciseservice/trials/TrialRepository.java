package eu.mostserene.avogador.exerciseservice.trials;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TrialRepository extends JpaRepository<Trial, UUID> {
    List<Trial> findByCourseIdAndIsVisibleTrue(UUID courseId);
    List<Trial> findByCourseId(UUID courseId);
}
