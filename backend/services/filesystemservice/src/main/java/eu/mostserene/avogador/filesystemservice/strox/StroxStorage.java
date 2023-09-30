package eu.mostserene.avogador.filesystemservice.strox;

import java.io.File;
import java.nio.file.Path;

public interface StroxStorage {

    void saveToFile(Strox path);

    Strox loadFromFile(Path path);

}
