package eu.mostserene.avogador.storageservice.exercises;

import eu.mostserene.avogador.storageservice.FileSystemRoot;
import eu.mostserene.avogador.storageservice.strox.Strox;
import eu.mostserene.avogador.storageservice.strox.StroxStorage;
import eu.mostserene.avogador.storageservice.strox.StroxStorageImpl;
import eu.mostserene.avogador.storageservice.testcases.TestcaseResponseTDO;
import eu.mostserene.avogador.storageservice.utils.*;
import eu.mostserene.avogador.storageservice.utils.FileNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Getter
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

    public File getBaseDirectory() {
        return new File(fileSystemRoot.getFileSystemRoot() + "/courses/" +
                getCourseId().toString() + "/trials/" + getTrialId().toString());
    }

    @Override
    public void create() {
        File exerciseFolder = new File(getBaseDirectory() + "/" + getExerciseId().toString());
        if (exerciseFolder.mkdirs()) {
            log.info(LoggerColors.success("Exercise " + getExerciseId() + ": folder created"));
        } else {
            throw new FileCreationFailed("Exercise " + getExerciseId() + ": folder creation failed");
        }

        File submissionsFolder = getSubmissionsFolder();
        if (submissionsFolder.mkdirs()) {
            log.info(LoggerColors.success("Exercise " + getExerciseId() + ": submissions folder created"));
        } else {
            throw new FileCreationFailed("Exercise " + getExerciseId() + ": submissions folder creation failed");
        }

        File testcasesFolder = getTestcasesFolder();
        if (testcasesFolder.mkdirs()) {
            log.info(LoggerColors.success("Exercise " + getExerciseId() + ": testcases folder created"));
        } else {
            throw new FileCreationFailed("Exercise " + getExerciseId() + ": testcases folder creation failed");
        }
    }

    @Override
    public File get() {
        return new File(getBaseDirectory() + "/" + getExerciseId().toString());
    }

    private File getSubmissionsFolder() {
        return new File(get().toString() + "/submissions");
    }

    private File getTestcasesFolder() {
        return new File(get().toString() + "/testcases");
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
        submission.setOutputs(new HashMap<>());
        stroxStorage.saveToFile(submission);
        String sourceCode = Strox.merge(getTemplate()
                        .orElseThrow(() -> new FileNotFoundException("Template of exercise " + submissionId + " not found")), submission)
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

    @Override
    public void saveTestcase(UUID testcaseId, String input, String output) {
        File inputFile = new File(getTestcasesFolder() + "/" + testcaseId + ".in");
        File outputFile = new File(getTestcasesFolder() + "/" + testcaseId + ".out");

        try {
            Files.writeString(inputFile.toPath(), input);
            Files.writeString(outputFile.toPath(), output);
        } catch (IOException e) {
            throw new FileCreationFailed("Testcase " + testcaseId + ": testcase files creation failed");
        }
    }

    @Override
    public Optional<TestcaseResponseTDO> getTestcase(UUID testcaseId) {
        File inputTestcase = new File(getTestcasesFolder() + "/" + testcaseId + ".in");
        File outputTestcase = new File(getTestcasesFolder() + "/" + testcaseId + ".out");

        if (!inputTestcase.exists() && !outputTestcase.exists()) return Optional.empty();

        if (!inputTestcase.exists() || !outputTestcase.exists()) {
            throw new IllegalStateException("A partial testcase is stored");
        }

        try {
            TestcaseResponseTDO testcaseResponseTDO = new TestcaseResponseTDO(testcaseId,
                    Files.readString(inputTestcase.toPath()), Files.readString(outputTestcase.toPath()));

            return Optional.of(testcaseResponseTDO);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read testcase files");
        }
    }

    @Override
    public Optional<File> getTestcases() {
        if (!getTestcasesFolder().exists()) {
            throw new FileNotFoundException("Exercise " + getExerciseId() + ": Testcases folder not found");
        }

        if (List.of(Objects.requireNonNull(getTestcasesFolder().listFiles())).isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(CompressionUtils.createTarGzipFolder(getTestcasesFolder().toPath()));
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

    @Override
    public void deleteTestcase(UUID testcaseId) {
        File inputFile = new File(getTestcasesFolder() + "/" + testcaseId + ".in");
        File outputFile = new File(getTestcasesFolder() + "/" + testcaseId + ".out");

        try {
            Files.deleteIfExists(inputFile.toPath());
            Files.deleteIfExists(outputFile.toPath());
        } catch (IOException e) {
            throw new FileDeletionFailed("Testcase " + testcaseId + ": testcase files deletion failed");
        }
    }

    @Override
    public File getSubmissionsCode(List<UUID> submissionIds) {
        try {
            File exportingDir = Files.createTempDirectory("submissions").toFile();
            File exportedDir = Files.createTempDirectory("exported").toFile();


            Arrays.stream(Objects.requireNonNull(getSubmissionsFolder().list()))
                    .filter(submissionFolderName ->
                            submissionIds.contains(UUID.fromString(submissionFolderName)))
                    .forEach(submissionFolderName -> {
                        log.info(LoggerColors.cyan(submissionFolderName));
                        File exportingSubmissionDirectory = new File(exportingDir + "/" + submissionFolderName);
                        exportingSubmissionDirectory.mkdirs();
                        log.info(LoggerColors.cyan(getSubmissionsFolder() + "/" + submissionFolderName + "/source"));
                        log.info(LoggerColors.cyan(exportingSubmissionDirectory.getPath()));

                        Arrays.stream(Objects.requireNonNull(new File(getSubmissionsFolder() + "/" + submissionFolderName + "/source").list()))
                                .forEach(s -> {
                                    try {
                                        FileUtils.copyToDirectory(
                                                new File(getSubmissionsFolder() + "/" + submissionFolderName + "/source/" + s)
                                                , exportingSubmissionDirectory);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });

                    });

            Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
            archiver.create("submissions", exportedDir, exportingDir);

            return new File(exportedDir + "/submissions.tar.gz");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveSimilarityReport(byte[] similarityReportBytes) {
        File archive = new File(get() + "/similarity.json");
        try {
            Files.write(archive.toPath(), similarityReportBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<File> getSimilarityReport() {
        File archive = new File(get() + "/similarity.json");
        return archive.exists() ? Optional.of(archive) : Optional.empty();
    }
}
