package eu.mostserene.avogador.storageservice.projects;

import java.io.File;

public interface ProjectStorage {
    void create();
    File get();
    void delete();
}
