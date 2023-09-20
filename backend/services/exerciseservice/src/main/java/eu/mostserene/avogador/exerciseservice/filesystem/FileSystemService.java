package eu.mostserene.avogador.exerciseservice.filesystem;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.trials.Trial;


public interface FileSystemService {
    void createTrial(Trial trial);
    void deleteTrial(Trial trial);
    void createExercise(Exercise exercise);
    void deleteExercise(Exercise exercise);
}
