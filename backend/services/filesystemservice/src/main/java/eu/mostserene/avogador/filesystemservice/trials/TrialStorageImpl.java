package eu.mostserene.avogador.filesystemservice.trials;

import eu.mostserene.avogador.filesystemservice.FileSystemRoot;
import eu.mostserene.avogador.filesystemservice.courses.CourseStorageImpl;
import eu.mostserene.avogador.filesystemservice.utils.LoggerColors;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;

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

    public UUID getCourseId() {
        return courseId;
    }

    public UUID getTrialId() {
        return trialId;
    }

    public File getBaseDirectory() {
        return new File(fileSystemRoot.getFileSystemRoot() + "/courses/" +
                getCourseId().toString() + "/trials/" );
    }

    @Override
    public void create() {
        File trialFolder = new File(getBaseDirectory() + "/" + getTrialId().toString());
        if (trialFolder.mkdirs()) {
            log.info(LoggerColors.success("Trial " + getTrialId() + ": folder created"));
        } else {
            log.error(LoggerColors.error("Trial " + getTrialId() + ": folder creation failed"));
        }
    }

    @Override
    public File get() {
        return new File(getBaseDirectory() + "/" + getTrialId().toString());
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException();
    }
}
