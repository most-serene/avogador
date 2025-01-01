package eu.mostserene.avogador.exerciseservice.exercises.multiplechoiceexercises;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MultipleChoiceOptionRepository extends JpaRepository<MultipleChoiceOption, UUID> {
    List<MultipleChoiceOption> findByExercise_Id(UUID id);
}
