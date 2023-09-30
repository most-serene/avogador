package eu.mostserene.avogador.filesystemservice.exercises;

import eu.mostserene.avogador.filesystemservice.FileSystemRoot;
import eu.mostserene.avogador.filesystemservice.strox.Strox;
import eu.mostserene.avogador.filesystemservice.strox.StroxStorage;
import eu.mostserene.avogador.filesystemservice.strox.StroxStorageImpl;
import eu.mostserene.avogador.filesystemservice.utils.LoggerColors;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

        File submissionsFolder = new File(exerciseFolder + "/submissions");
        if (submissionsFolder.mkdirs()) {
            log.info(LoggerColors.success("Exercise " + getExerciseId() + ": submissions folder created"));
        } else {
            log.error(LoggerColors.error("Exercise " + getExerciseId() + ": submissions folder creation failed"));
        }
    }

    @Override
    public File get() {
        return new File(getBaseDirectory() + "/" + getExerciseId().toString());
    }

    private File getSubmissionsFolder() {
        return new File(get().toString() + "/submissions");
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
    public void saveSubmission(UUID submissionId, Strox submission) {
        StroxStorage stroxStorage = new StroxStorageImpl();
        createSubmission(submissionId);
        submission.setPath(getSubmissionsFolder() + "/" + submissionId + "/submission.strox");
        stroxStorage.saveToFile(submission);
        String sourceCode = Strox.merge(getTemplate(), submission)
                .generateSourceCode();

        File sourceFile = new File(getSubmissionsFolder() + "/" + submissionId + "/source/" + submission.getSourceFileName());

        try {
            Files.writeString(sourceFile.toPath(), sourceCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createSubmission(UUID submissionId) {
        File submissionFolder = new File(getSubmissionsFolder() + "/" + submissionId);
        if (!submissionFolder.exists()) {
            if (submissionFolder.mkdirs()) {
                log.info(LoggerColors.success("Submission " + submissionId + ": folder created"));
            } else {
                log.error(LoggerColors.error("Submission " + submissionId + ": folder creation failed"));
            }
        }

        File sourceCodeFolder = new File(submissionFolder + "/source");
        if (!sourceCodeFolder.exists()) {
            if (sourceCodeFolder.mkdirs()) {
                log.info(LoggerColors.success("Submission " + submissionId + ": sourcecode folder created"));
            } else {
                log.error(LoggerColors.error("Submission " + submissionId + ": soucecode folder creation failed"));
            }
        }
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException();
    }
}
