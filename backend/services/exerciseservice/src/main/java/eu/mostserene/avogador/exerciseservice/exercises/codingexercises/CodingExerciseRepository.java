package eu.mostserene.avogador.exerciseservice.exercises.codingexercises;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CodingExerciseRepository extends JpaRepository<CodingExercise, UUID> {
}
