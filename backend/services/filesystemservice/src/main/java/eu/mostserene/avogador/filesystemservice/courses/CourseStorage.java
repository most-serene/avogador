package eu.mostserene.avogador.filesystemservice.courses;

import java.io.File;

public interface CourseStorage {

    void create();

    File get();

    void archive();

    void delete();

}
