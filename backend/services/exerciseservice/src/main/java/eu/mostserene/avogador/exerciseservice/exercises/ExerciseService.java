package eu.mostserene.avogador.exerciseservice.exercises;

import java.util.Optional;
import java.util.UUID;

public interface ExerciseService {
    Optional<Exercise> getExercise(UUID exerciseId);
    Exercise createExercise(ExerciseDto exerciseDto);
    Exercise updateExercise(Exercise exercise);
    void deleteExercise(Exercise exercise);
}
