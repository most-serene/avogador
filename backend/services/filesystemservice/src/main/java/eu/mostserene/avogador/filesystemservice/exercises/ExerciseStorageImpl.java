package eu.mostserene.avogador.filesystemservice.exercises;

import eu.mostserene.avogador.filesystemservice.FileSystemRoot;
import eu.mostserene.avogador.filesystemservice.strox.Strox;
import eu.mostserene.avogador.filesystemservice.strox.StroxStorage;
import eu.mostserene.avogador.filesystemservice.strox.StroxStorageImpl;
import eu.mostserene.avogador.filesystemservice.utils.CompressionUtils;
import eu.mostserene.avogador.filesystemservice.utils.FileCreationFailed;
import eu.mostserene.avogador.filesystemservice.utils.FileNotFound;
import eu.mostserene.avogador.filesystemservice.utils.LoggerColors;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.*;
import java.util.Optional;
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
            throw new FileCreationFailed("Exercise " + getExerciseId() + ": folder creation failed");
        }

        File submissionsFolder = new File(exerciseFolder + "/submissions");
        if (submissionsFolder.mkdirs()) {
            log.info(LoggerColors.success("Exercise " + getExerciseId() + ": submissions folder created"));
        } else {
            throw new FileCreationFailed("Exercise " + getExerciseId() + ": submissions folder creation failed");
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
    public Optional<Strox> getTemplate() {
        StroxStorage storage = new StroxStorageImpl();
        return storage.loadFromFile(Path.of(get().toString() + "/template.strox"));
    }

    @Override
    public void saveSubmission(UUID submissionId, Strox submission) {
        StroxStorage stroxStorage = new StroxStorageImpl();
        File submissionFolder = createSubmissionFolder(submissionId);
        submission.setPath(submissionFolder + "/submission.strox");
        stroxStorage.saveToFile(submission);
        String sourceCode = Strox.merge(getTemplate()
                        .orElseThrow(() -> new FileNotFound("Template of exercise " + submissionId + " not found")), submission)
                .generateSourceCode();

        File sourceFile = new File(submissionFolder + "/source/" + submission.getSourceFileName());

        try {
            Files.writeString(sourceFile.toPath(), sourceCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Strox> getSubmissionStrox(UUID submissionId) {
        StroxStorage stroxStorage = new StroxStorageImpl();
        return stroxStorage.loadFromFile(Path.of(getSubmissionsFolder() + "/" + submissionId + "/submission.strox"));
    }

    @Override
    public Optional<File> getSubmissionCode(UUID submissionId) {
        File submissionFolder = new File(getSubmissionsFolder() + "/" + submissionId);
        if (!new File(submissionFolder + "/source").exists()) return Optional.empty();

        try {
            return Optional.of(CompressionUtils.createTarGzipFolder(Path.of(submissionFolder + "/source")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File createSubmissionFolder(UUID submissionId) {
        File submissionFolder = new File(getSubmissionsFolder() + "/" + submissionId);
        if (!submissionFolder.exists()) {
            if (submissionFolder.mkdirs()) {
                log.info(LoggerColors.success("Submission " + submissionId + ": folder created"));
            } else {
                throw new FileCreationFailed("Submission " + submissionId + ": folder creation failed");
            }
        }

        File sourceCodeFolder = new File(submissionFolder + "/source");
        if (!sourceCodeFolder.exists()) {
            if (sourceCodeFolder.mkdirs()) {
                log.info(LoggerColors.success("Submission " + submissionId + ": sourcecode folder created"));
            } else {
                throw new FileCreationFailed("Submission " + submissionId + ": soucecode folder creation failed");
            }
        }

        return submissionFolder;
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException();
    }
}
