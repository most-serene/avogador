package eu.mostserene.avogador.filesystemservice;

import eu.mostserene.avogador.filesystemservice.utils.LoggerColors;
import eu.mostserene.avogador.filesystemservice.utils.ServerInitError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@Component
public class FileSystemRoot {

    private static FileSystemRoot instance;

    private static final String fileSystemRoot = System.getenv("fs.root");


    public static FileSystemRoot getInstance() {
        if (FileSystemRoot.instance == null) {
            FileSystemRoot.instance = new FileSystemRoot();
            FileSystemRoot.instance.initFileSystem();
        }
        return FileSystemRoot.instance;
    }

    private void initFileSystem() {
        createCourseBaseFolder();
    }

    private void createCourseBaseFolder() {
        File coursesBaseFolder = new File(fileSystemRoot + "/courses/");
        try {
            Files.createDirectories(coursesBaseFolder.toPath());
            log.info(LoggerColors.success("Course base folder created"));
        } catch (IOException e) {
            throw new ServerInitError("Course base folder creation failed");
        }
    }

    public File getFileSystemRoot() {
        return new File(fileSystemRoot);
    }
}
