package eu.mostserene.avogador.exerciseservice.abstractexercises;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AbstractExerciseRepository extends JpaRepository<AbstractExercise, UUID> {
}
