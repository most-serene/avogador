package eu.mostserene.avogador.storageservice.projects;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public interface ProjectStorage {
    void create();

    File get();

    void saveSubmission(UUID submissionId, MultipartFile projectSubmission);

    Optional<File> getSubmission(UUID submissionId);

    void addFileToSubmission(UUID submissionId, String filename, MultipartFile file);

    Optional<File> getAdditionalFile(UUID submissionId, String filename);

    void delete();
}
