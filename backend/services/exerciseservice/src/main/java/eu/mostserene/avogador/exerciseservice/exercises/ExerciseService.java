package eu.mostserene.avogador.exerciseservice.exercises;

import eu.mostserene.avogador.exerciseservice.abstractexercises.AbstractExercise;
import eu.mostserene.avogador.exerciseservice.trials.Trial;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExerciseService {
    Optional<AbstractExercise> getExercise(UUID exerciseId);

    List<AbstractExercise> getExercisesFromTrial(Trial trial, Boolean includeHidden);

    void deleteExercise(AbstractExercise exercise);

    void deleteExercisesByTrial(Trial trial);

}
