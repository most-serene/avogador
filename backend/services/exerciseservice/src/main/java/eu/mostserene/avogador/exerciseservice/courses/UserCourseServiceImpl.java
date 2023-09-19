package eu.mostserene.avogador.exerciseservice.courses;

import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.Optional;
import java.util.UUID;

public class UserCourseServiceImpl implements UserCourseService {
    @Override
    public Optional<CourseRole> getUserCourse(UUID courseId, UUID userId) {
        CourseRole courseRole = new RestTemplateBuilder()
                .build()
                .getForObject("http://courses/" + courseId + "/users/" + userId, CourseRole.class);

        return courseRole != null ? Optional.of(courseRole) : Optional.empty();
    }
}
