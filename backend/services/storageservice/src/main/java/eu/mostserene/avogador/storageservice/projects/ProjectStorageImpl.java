package eu.mostserene.avogador.storageservice.projects;

import eu.mostserene.avogador.storageservice.FileSystemRoot;
import eu.mostserene.avogador.storageservice.utils.FileCreationFailed;
import eu.mostserene.avogador.storageservice.utils.LoggerColors;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.UUID;

@Getter
@Slf4j
public class ProjectStorageImpl implements ProjectStorage {
    private final UUID courseId;
    private final UUID projectId;
    private static final FileSystemRoot fileSystemRoot = FileSystemRoot.getInstance();

    public static ProjectStorage of(@NotNull UUID courseId, @NotNull UUID projectId) {
        return new ProjectStorageImpl(courseId, projectId);
    }

    private ProjectStorageImpl(UUID courseId, UUID projectId) {
        this.courseId = courseId;
        this.projectId = projectId;
    }

    public File getBaseDirectory() {
        return new File(fileSystemRoot.getFileSystemRoot() + "/courses/" +
                getCourseId().toString() + "/projects" );
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
    public void delete() {
        log.error(LoggerColors.error("Not implemented yet"));
    }
}
