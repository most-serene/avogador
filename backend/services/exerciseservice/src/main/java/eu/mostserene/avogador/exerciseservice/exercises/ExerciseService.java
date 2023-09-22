package eu.mostserene.avogador.exerciseservice.exercises;

import eu.mostserene.avogador.exerciseservice.trials.Trial;

import java.util.Optional;
import java.util.UUID;

public interface ExerciseService {
    Optional<Exercise> getExercise(UUID exerciseId);
    Exercise createExercise(ExerciseDto exerciseDto, Trial trial);
    Exercise updateExercise(Exercise exercise);
    void deleteExercise(Exercise exercise);
}
