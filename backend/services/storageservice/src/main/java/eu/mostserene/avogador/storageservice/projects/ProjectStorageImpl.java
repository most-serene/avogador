package eu.mostserene.avogador.storageservice.projects;

import eu.mostserene.avogador.storageservice.FileSystemRoot;
import eu.mostserene.avogador.storageservice.utils.FileCreationFailed;
import eu.mostserene.avogador.storageservice.utils.LoggerColors;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Getter
@Slf4j
public class ProjectStorageImpl implements ProjectStorage {
    private static final FileSystemRoot fileSystemRoot = FileSystemRoot.getInstance();
    private final UUID courseId;
    private final UUID projectId;

    private ProjectStorageImpl(UUID courseId, UUID projectId) {
        this.courseId = courseId;
        this.projectId = projectId;
    }

    public static ProjectStorage of(@NotNull UUID courseId, @NotNull UUID projectId) {
        return new ProjectStorageImpl(courseId, projectId);
    }

    public File getBaseDirectory() {
        return new File(fileSystemRoot.getFileSystemRoot() + "/courses/" +
                getCourseId().toString() + "/projects");
    }

    private File getProjectDirectory() {
        return new File(getBaseDirectory() + "/" + getProjectId().toString());
    }


    @Override
    public void create() {
        if (getProjectDirectory().mkdirs()) {
            log.info(LoggerColors.success("Project " + getProjectId() + ": folder created"));
        } else {
            throw new FileCreationFailed("Project " + getProjectId() + ": folder creation failed");
        }
    }

    @Override
    public File get() {
        return new File(getBaseDirectory() + "/" + getProjectId().toString());
    }

    @Override
    public void saveSubmission(UUID submissionId, MultipartFile projectSubmission) {
        File submissionFolder = createSubmissionFolder(submissionId);
        File submissionFile = new File(submissionFolder + "/submission.tar.gz");
        try {
            projectSubmission.transferTo(submissionFile);
        } catch (IOException e) {
            throw new FileCreationFailed("ProjectSubmission " + submissionId + ": submission archive creation failed");
        }
    }

    @Override
    public Optional<File> getSubmission(UUID submissionId) {
        File submission = new File(getProjectDirectory() + "/" + submissionId + "/submission.tar.gz");
        return submission.exists() ? Optional.of(submission) : Optional.empty();
    }

    @Override
    public void addFileToSubmission(UUID submissionId, String filename, MultipartFile file) {
        File submissionFolder = new File(getProjectDirectory() + "/" + submissionId);
        if (!submissionFolder.exists()) {
            throw new RuntimeException("ProjectSubmission " + submissionId + ": submission not found");
        }
        checkFilenameValidity(filename);
        try {
            file.transferTo(new File(submissionFolder + "/" + filename));
        } catch (IOException e) {
            throw new FileCreationFailed("ProjectSubmission " + submissionId + ": additional file saving failed");
        }
    }

    @Override
    public Optional<File> getAdditionalFile(UUID submissionId, String filename) {
        checkFilenameValidity(filename);
        File file = new File(getProjectDirectory() + "/" + submissionId + "/" + filename);
        return file.exists() ? Optional.of(file) : Optional.empty();
    }

    private void checkFilenameValidity(String filename) {
        if (!filename.matches("^[\\w,\\s-]+\\.[A-Za-z]+$")) {
            throw new RuntimeException("Invalid filename: " + filename);
        }
    }


    private File createSubmissionFolder(UUID submissionId) {
        File submissionFolder = new File(getProjectDirectory() + "/" + submissionId);

        if (submissionFolder.exists() || submissionFolder.mkdirs()) {
            log.info(LoggerColors.success("Project " + getProjectId() + ": folder already existing or created"));
        } else {
            throw new FileCreationFailed("Project " + getProjectId() + ": folder creation failed");
        }
        return submissionFolder;
    }

    @Override
    public void delete() {
        log.error(LoggerColors.error("Not implemented yet"));
    }
}
