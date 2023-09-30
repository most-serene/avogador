package eu.mostserene.avogador.filesystemservice.exercises;

import eu.mostserene.avogador.filesystemservice.FileSystemRoot;
import eu.mostserene.avogador.filesystemservice.strox.Strox;
import eu.mostserene.avogador.filesystemservice.strox.StroxStorage;
import eu.mostserene.avogador.filesystemservice.strox.StroxStorageImpl;
import eu.mostserene.avogador.filesystemservice.utils.LoggerColors;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
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
    public void saveTemplate(Strox template) {
        StroxStorage storage = new StroxStorageImpl();
        template.setPath(get().toString() + "/template.strox");
        storage.saveToFile(template);
    }

    @Override
    public Strox getTemplate() {
        StroxStorage storage = new StroxStorageImpl();
        return storage.loadFromFile(Path.of(get().toString() + "/template.strox"));
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException();
    }
}
