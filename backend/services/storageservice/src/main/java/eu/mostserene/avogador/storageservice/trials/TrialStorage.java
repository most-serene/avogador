package eu.mostserene.avogador.storageservice.trials;

import java.io.File;

public interface TrialStorage {
    void create();

    File get();

    void delete();
}
