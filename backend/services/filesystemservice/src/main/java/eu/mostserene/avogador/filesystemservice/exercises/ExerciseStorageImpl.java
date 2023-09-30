package eu.mostserene.avogador.filesystemservice.exercises;

import eu.mostserene.avogador.filesystemservice.FileSystemRoot;
import eu.mostserene.avogador.filesystemservice.utils.LoggerColors;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.UUID;

@Slf4j
public class ExerciseStorageImpl implements ExerciseStorage {
    private final UUID courseId;
    private final UUID trialId;
    private final UUID exerciseId;

    private static final FileSystemRoot fileSystemRoot = FileSystemRoot.getInstance();

    public static ExerciseStorage of(@NotNull UUID courseId, @NotNull UUID trialId, @NotNull UUID exerciseId) {
        return new ExerciseStorageImpl(courseId, trialId, exerciseId);
    }

    private ExerciseStorageImpl(UUID courseId, UUID trialId, UUID exerciseId) {
        this.courseId = courseId;
        this.trialId = trialId;
        this.exerciseId = exerciseId;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public UUID getTrialId() {
        return trialId;
    }

    public UUID getExerciseId() {
        return exerciseId;
    }

    public File getBaseDirectory() {
        return new File(fileSystemRoot.getFileSystemRoot() + "/courses/" +
                getCourseId().toString() + "/trials/" + getTrialId().toString() );
    }

    @Override
    public void create() {
        File exerciseFolder = new File(getBaseDirectory() + "/" + getExerciseId().toString());
        if (exerciseFolder.mkdirs()) {
            log.info(LoggerColors.success("Exercise " + getExerciseId() + ": folder created"));
        } else {
            log.error(LoggerColors.error("Exercise " + getExerciseId() + ": folder creation failed"));
        }
    }

    @Override
    public File get() {
        return new File(getBaseDirectory() + "/" + getExerciseId().toString());
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException();
    }
}
