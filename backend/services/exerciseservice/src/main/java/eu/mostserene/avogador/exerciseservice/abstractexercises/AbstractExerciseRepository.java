package eu.mostserene.avogador.exerciseservice.abstractexercises;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AbstractExerciseRepository extends JpaRepository<AbstractExercise, UUID> {
    List<AbstractExercise> findByTrial_IdAndIsVisibleTrue(UUID id);

    List<AbstractExercise> findByTrial_Id(UUID id);
}
