package eu.mostserene.avogador.storageservice.strox;

import java.nio.file.Path;
import java.util.Optional;

public interface StroxStorage {

    void saveToFile(Strox path);

    Optional<Strox> loadFromFile(Path path);

}
