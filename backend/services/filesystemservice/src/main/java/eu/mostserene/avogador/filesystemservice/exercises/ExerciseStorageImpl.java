package eu.mostserene.avogador.filesystemservice.exercises;

import eu.mostserene.avogador.filesystemservice.FileSystemRoot;
import eu.mostserene.avogador.filesystemservice.strox.Strox;
import eu.mostserene.avogador.filesystemservice.strox.StroxStorage;
import eu.mostserene.avogador.filesystemservice.strox.StroxStorageImpl;
import eu.mostserene.avogador.filesystemservice.utils.LoggerColors;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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

    @Override
    public Strox getSubmissionStrox(UUID submissionId) {
        StroxStorage stroxStorage = new StroxStorageImpl();
        return stroxStorage.loadFromFile(Path.of(getSubmissionsFolder() + "/" + submissionId + "/submission.strox"));
    }

    @Override
    public File getSubmissionCode(UUID submissionId) {
        File submissionFolder = new File(getSubmissionsFolder() + "/" + submissionId);

        try {
            createTarGzipFolder(Path.of(submissionFolder + "/source"));
            return new File(submissionFolder + "/source.tar.gz");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void createTarGzipFolder(Path source) throws IOException {
        if (!Files.isDirectory(source)) {
            throw new IOException("Please provide a directory.");
        }

        String tarFileName = source.getParent() + "/" + source.getFileName().toString() + ".tar.gz";
        try (OutputStream fOut = Files.newOutputStream(Path.of(tarFileName));
             BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
             GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(buffOut);
             TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {

            Files.walkFileTree(source, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult visitFile(Path file,
                                                 BasicFileAttributes attributes) {

                    if (attributes.isSymbolicLink()) {
                        return FileVisitResult.CONTINUE;
                    }
                    Path targetFile = source.relativize(file);
                    try {
                        TarArchiveEntry tarEntry = new TarArchiveEntry(
                                file.toFile(), targetFile.toString());
                        tOut.putArchiveEntry(tarEntry);
                        Files.copy(file, tOut);
                        tOut.closeArchiveEntry();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
            tOut.finish();
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
