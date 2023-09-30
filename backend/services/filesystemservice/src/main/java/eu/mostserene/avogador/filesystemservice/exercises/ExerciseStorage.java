package eu.mostserene.avogador.filesystemservice.exercises;

import eu.mostserene.avogador.filesystemservice.strox.Strox;

import java.io.File;

public interface ExerciseStorage {
    void create();

    File get();

    void saveTemplate(Strox template);

    void delete();
}
