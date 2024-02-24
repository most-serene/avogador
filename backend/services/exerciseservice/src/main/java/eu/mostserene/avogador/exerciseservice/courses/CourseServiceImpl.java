package eu.mostserene.avogador.exerciseservice.courses;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class CourseServiceImpl implements CourseService {
    @Override
    public Optional<CourseDto> getCourseById(UUID courseId) {
        try {
            CourseDto course = new RestTemplateBuilder()
                    .build()
                    .getForObject("http://courses/courses/" + courseId, CourseDto.class);

            return course != null ? Optional.of(course) : Optional.empty();
        } catch (Exception exception) {
            return Optional.empty();
        }
    }
}
