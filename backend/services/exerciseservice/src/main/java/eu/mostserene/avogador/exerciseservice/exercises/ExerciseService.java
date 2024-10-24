package eu.mostserene.avogador.exerciseservice.exercises;

import eu.mostserene.avogador.exerciseservice.trials.Trial;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExerciseService {
    Optional<Exercise> getExercise(UUID exerciseId);

    List<Exercise> getExercisesFromTrial(Trial trial, Boolean includeHidden);

    void deleteExercise(Exercise exercise);

    void deleteExercisesByTrial(Trial trial);

}
