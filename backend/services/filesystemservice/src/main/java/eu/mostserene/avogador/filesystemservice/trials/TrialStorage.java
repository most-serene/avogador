package eu.mostserene.avogador.filesystemservice.trials;

import java.io.File;

public interface TrialStorage {
    void create();

    File get();

    void delete();
}
