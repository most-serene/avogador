package eu.mostserene.avogador.storageservice.trials;

import eu.mostserene.avogador.storageservice.FileSystemRoot;
import eu.mostserene.avogador.storageservice.logger.AvogadorLogMessage;
import eu.mostserene.avogador.storageservice.utils.FileCreationFailed;
import eu.mostserene.avogador.storageservice.utils.LoggerColors;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@Getter
@Slf4j
public class TrialStorageImpl implements TrialStorage {

    private final UUID courseId;
    private final UUID trialId;

    private static final FileSystemRoot fileSystemRoot = FileSystemRoot.getInstance();

    public static TrialStorage of(@NotNull UUID courseId, @NotNull UUID trialId) {
        return new TrialStorageImpl(courseId, trialId);
    }

    private TrialStorageImpl(UUID courseId, UUID trialId) {
        this.courseId = courseId;
        this.trialId = trialId;
    }

    public File getBaseDirectory() {
        return new File(fileSystemRoot.getFileSystemRoot() + "/courses/" +
                getCourseId().toString() + "/trials/" );
    }

    private File getTrialDirectory() {
        return new File(getBaseDirectory() + "/" + getTrialId().toString());
    }

    @Override
    public void create() {
        if (getTrialDirectory().mkdirs()) {
            log.info(LoggerColors.success("Trial " + getTrialId() + ": folder created"));
        } else {
            throw new FileCreationFailed("Trial " + getTrialId() + ": folder creation failed");
        }
    }

    @Override
    public File get() {
        return new File(getBaseDirectory() + "/" + getTrialId().toString());
    }

    @Override
    public void appendLog(AvogadorLogMessage avogadorLogMessage) throws IOException {
        File logFile = new File(getTrialDirectory() + "/" + getTrialId() + ".avlog");
        if (logFile.createNewFile()) {
            log.info(LoggerColors.success("Trial " + getTrialId() + ": log file created"));
        }
        FileUtils.writeStringToFile(logFile, avogadorLogMessage.toString(), StandardCharsets.UTF_8, true);
    }

    @Override
    public List<String> getLogs() throws IOException {
        File logFile = new File(getTrialDirectory() + "/" + getTrialId() + ".avlog");
        return Files.readAllLines(logFile.toPath());
    }

    @Override
    public void delete() {
        try {
            FileUtils.deleteDirectory(get());
        } catch (IOException e) {
            log.error(LoggerColors.error("Trial " + getTrialId() + ": trial deletion failed"));
        }
    }
}
