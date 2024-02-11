package eu.mostserene.avogador.storageservice.trials;

import eu.mostserene.avogador.storageservice.logger.AvogadorLogMessage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface TrialStorage {
    void create();
    File get();
    void appendLog(AvogadorLogMessage avogadorLogMessage) throws IOException;
    List<String> getLogs() throws IOException;
    void delete();
}
