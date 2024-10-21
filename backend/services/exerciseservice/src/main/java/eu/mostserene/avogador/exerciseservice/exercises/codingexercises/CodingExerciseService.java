package eu.mostserene.avogador.exerciseservice.exercises.codingexercises;

import eu.mostserene.avogador.exerciseservice.trials.Trial;

import java.util.Optional;
import java.util.UUID;

public interface CodingExerciseService {
    Optional<CodingExercise> getCodingExercise(UUID exerciseId);

    CodingExercise createCodingExercise(CodingExerciseDto codingExerciseDto, Trial trial);

    CodingExercise updateCodingExercise(CodingExercise codingExercise);

    void deleteCodingExercise(CodingExercise codingExercise);
}
