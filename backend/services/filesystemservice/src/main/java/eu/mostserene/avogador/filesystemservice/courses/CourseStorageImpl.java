package eu.mostserene.avogador.filesystemservice.courses;

import eu.mostserene.avogador.filesystemservice.FileSystemRoot;
import eu.mostserene.avogador.filesystemservice.utils.LoggerColors;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.UUID;

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

    public UUID getCourseId() {
        return courseId;
    }

    @Override
    public void create() {
        File courseFolder = new File(getBaseDirectory() + "/" + getCourseId().toString());
        if (courseFolder.mkdir()) {
            log.info(LoggerColors.success("Course " + this.getCourseId() + ": folder created"));
        } else {
            log.error(LoggerColors.error("Course " + this.getCourseId() + ": folder creation failed"));
        }
    }

    @Override
    public File get() {
        return new File(getBaseDirectory() + "/" + getCourseId().toString());
    }

    @Override
    public void archive() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException();
    }
}
