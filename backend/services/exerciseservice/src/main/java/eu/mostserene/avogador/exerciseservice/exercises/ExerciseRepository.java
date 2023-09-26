package eu.mostserene.avogador.exerciseservice.exercises;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
    List<Exercise> findByTrial_IdAndIsVisibleTrue(UUID id);
    List<Exercise> findByTrial_Id(UUID id);
}
