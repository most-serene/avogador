package eu.mostserene.avogador.storageservice.courses;

import java.io.File;
import java.util.Optional;

public interface CourseStorage {
    void create();
    File get();
    boolean archive();
    Optional<File> getArchive();
    void delete();
}
