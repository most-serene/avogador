package eu.mostserene.avogador.filesystemservice.strox;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface StroxStorage {

    File saveToFile(Strox path);

    Strox loadFromFile(Path path);

}
