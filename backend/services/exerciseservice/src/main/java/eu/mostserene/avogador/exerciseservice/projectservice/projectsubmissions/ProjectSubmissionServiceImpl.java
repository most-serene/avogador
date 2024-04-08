package eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions;

import eu.mostserene.avogador.exerciseservice.projectservice.projects.Project;
import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.utils.LoggerColors;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Transactional
@Service
public class ProjectSubmissionServiceImpl implements ProjectSubmissionService {

    @Autowired
    private ProjectSubmissionRepository repository;

    @Autowired
    private StorageService storageService;

    @Override
    public ProjectSubmission createSubmission(Project project, UserDto user, MultipartFile file) {
        ProjectSubmission submission = repository.save(new ProjectSubmission(project, user.getId()));

        if (submission.getTimestamp().after(project.getDeadline())) {
            // TODO: we should to something if a user submits after the deadline
            log.warn(LoggerColors.warn("Submitting after deadline"));
        }
        // storageService.deleteProjectSubmission(submission);
        /* TODO: to optimize storage, we could delete the previous submission files
                however we'd loose all the extra files linked to that submission, like evalutations,
                reports and stuff like that
         */

        try {
            storageService.createProjectSubmission(submission, convertZipToTarGz(submission, file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return submission;
    }

    private File convertZipToTarGz(ProjectSubmission submission, MultipartFile file) throws IOException {
        File tempDir = Files.createTempDirectory(submission.getId().toString()).toFile();
        File zipArchive = new File(tempDir + "/archive.zip");
        file.transferTo(zipArchive);

        Archiver zipArchiver = ArchiverFactory.createArchiver("zip");
        zipArchiver.extract(zipArchive, new File(tempDir + "/project"));

        Archiver tarGzArchiver = ArchiverFactory.createArchiver("tar", "gz");
        tarGzArchiver.create("project", tempDir, new File(tempDir + "/project"));

        return new File(tempDir + "/project.tar.gz");
    }

    @Override
    public Optional<ProjectSubmission> getProjectSubmissionById(UUID submissionId) {
        return repository.findById(submissionId);
    }

    @Override
    public ProjectSubmission setProjectSubmissionStatus(UUID submissionId, ProjectStatus status) {
        ProjectSubmission submission = getProjectSubmissionById(submissionId)
                .orElseThrow(NotFoundException::new);

        submission.setStatus(status);

        return repository.save(submission);
    }

    @Override
    public List<ProjectSubmission> getUserSubmissions(Project project, UserDto user) {
        return repository.findByProject_IdAndUserId(project.getId(), user.getId());
    }
}
