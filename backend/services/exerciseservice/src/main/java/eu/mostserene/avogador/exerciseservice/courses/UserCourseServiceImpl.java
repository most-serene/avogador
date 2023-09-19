package eu.mostserene.avogador.exerciseservice.courses;

import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.Optional;
import java.util.UUID;

public class UserCourseServiceImpl implements UserCourseService {
    @Override
    public Optional<UserCourseDto> getUserCourse(UUID courseId, UUID userId) {
        UserCourseDto userCourse = new RestTemplateBuilder()
                .build()
                .getForObject("http://courses/" + courseId + "/users/" + userId, UserCourseDto.class);

        return userCourse != null ? Optional.of(userCourse) : Optional.empty();
    }
}
