package eu.mostserene.avogador.courseservice.storage;

import eu.mostserene.avogador.courseservice.amqp.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService {

    @Autowired
    private Sender sender;

    @Override
    public void createCourse(UUID courseId) {
        sender.send("storage", "storage.course.create", courseId.toString());
    }

    @Override
    public Integer deleteCourse(UUID courseId) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public Integer archiveCourse(UUID courseId) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
}
