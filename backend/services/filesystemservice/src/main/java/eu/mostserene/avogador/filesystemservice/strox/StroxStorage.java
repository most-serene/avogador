package eu.mostserene.avogador.filesystemservice.strox;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public interface StroxStorage {

    void saveToFile(Strox path);

    Optional<Strox> loadFromFile(Path path);

}
