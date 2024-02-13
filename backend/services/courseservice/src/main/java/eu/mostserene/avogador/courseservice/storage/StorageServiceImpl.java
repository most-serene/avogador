package eu.mostserene.avogador.courseservice.storage;

import eu.mostserene.avogador.courseservice.amqp.Sender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.BiConsumer;

@Service
@Slf4j
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
    public void archiveCourse(UUID courseId, BiConsumer<Boolean, Throwable> handler) {
        sender.send("storage", "storage.course.archive", courseId.toString(), handler);
    }
}
