package eu.mostserene.avogador.storageservice.courses;

import eu.mostserene.avogador.storageservice.FileSystemRoot;
import eu.mostserene.avogador.storageservice.utils.CompressionUtils;
import eu.mostserene.avogador.storageservice.utils.FileCreationFailed;
import eu.mostserene.avogador.storageservice.utils.LoggerColors;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Getter
@Slf4j
public class CourseStorageImpl implements CourseStorage {
    private final UUID courseId;

    private static final FileSystemRoot fileSystemRoot = FileSystemRoot.getInstance();

    public static File getBaseDirectory() {
        return new File(fileSystemRoot.getFileSystemRoot() + "/courses/");
    }

    public static CourseStorage of(@NotNull UUID courseId) {
        return new CourseStorageImpl(courseId);
    }

    private CourseStorageImpl(UUID courseId) {
        this.courseId = courseId;
    }

    @Override
    public void create() {
        File courseFolder = new File(getBaseDirectory() + "/" + getCourseId().toString());
        if (courseFolder.mkdir()) {
            log.info(LoggerColors.success("Course " + this.getCourseId() + ": folder created"));
        } else {
            throw new FileCreationFailed("Course " + this.getCourseId() + ": folder creation failed");
        }
    }

    @Override
    public File get() {
        return new File(getBaseDirectory() + "/" + getCourseId().toString());
    }

    @Override
    public boolean archive() {
        try {
            File archive = CompressionUtils.createTarGzipFolder(get().toPath());
            boolean isRenamed = archive.renameTo(
                    new File(archive.getParentFile() + "/" + getCourseId().toString() + "-final.tar.gz")
            );
            if (!isRenamed) {
                FileUtils.deleteQuietly(archive);
                throw new RuntimeException("Course archive renaming failed");
            }
            FileUtils.deleteDirectory(get());
            log.info(LoggerColors.success("Course " + this.getCourseId() + ": archive created and folder deleted"));
            return true;
        } catch (Exception exception) {
            log.error(LoggerColors.error(exception.toString()));
            return false;
        }
    }

    @Override
    public Optional<File> getArchive() {
        File archive = new File(getBaseDirectory() + "/" + getCourseId().toString() + "-final.tar.gz");
        if (archive.exists()) {
            return Optional.of(archive);
        }
        return Optional.empty();
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException();
    }
}
