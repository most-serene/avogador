package eu.mostserene.avogador.filesystemservice.strox;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StroxStorageImpl implements StroxStorage {
    @Override
    public void saveToFile(Strox strox) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String content = mapper.writeValueAsString(strox);
            Files.writeString(Path.of(strox.getPath()), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Strox loadFromFile(Path path) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(Files.readString(path), Strox.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
