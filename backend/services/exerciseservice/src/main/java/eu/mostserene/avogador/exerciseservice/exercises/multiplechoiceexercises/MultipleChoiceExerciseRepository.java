package eu.mostserene.avogador.exerciseservice.exercises.multiplechoiceexercises;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MultipleChoiceExerciseRepository extends JpaRepository<MultipleChoiceExercise, UUID> {
}
