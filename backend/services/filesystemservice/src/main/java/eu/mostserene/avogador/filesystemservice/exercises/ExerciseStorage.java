package eu.mostserene.avogador.filesystemservice.exercises;

import java.io.File;

public interface ExerciseStorage {
    void create();

    File get();

    void delete();
}
