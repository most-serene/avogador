package eu.mostserene.avogador.courseservice.storage;

import eu.mostserene.avogador.courseservice.amqp.Sender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;
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

    @Override
    public Optional<Resource> getCourseArchive(UUID courseId) {
        try {
            Resource courseArchive = new RestTemplateBuilder()
                    .build()
                    .getForObject("http://storage/courses/" + courseId + "/archive", Resource.class);

            if (courseArchive == null) {
                return Optional.empty();
            }
            return Optional.of(courseArchive);
        } catch (HttpClientErrorException.NotFound notFoundException) {
            return Optional.empty();
        }
    }
}
