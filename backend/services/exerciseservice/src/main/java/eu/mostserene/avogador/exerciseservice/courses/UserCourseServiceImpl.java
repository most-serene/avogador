package eu.mostserene.avogador.exerciseservice.courses;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserCourseServiceImpl implements UserCourseService {
    @Override
    public List<UserCourseDto> getCourseCollaborators(UUID courseId) {
        return Objects.requireNonNull(
                new RestTemplateBuilder().build()
                        .getForObject("http://courses/courses/" + courseId + "/collaborators",
                                UserCourseDtoList.class)
        ).getUserCourses();
    }

    @Override
    public Optional<CourseRole> getUserCourseRole(UUID courseId, UUID userId) {
        CourseRole courseRole = new RestTemplateBuilder()
                .build()
                .getForObject("http://courses/courses/" + courseId + "/users/" + userId, CourseRole.class);

        return courseRole != null ? Optional.of(courseRole) : Optional.empty();
    }

    @Data
    private static class UserCourseDtoList {
        private List<UserCourseDto> userCourses;
        public UserCourseDtoList() {
        }
    }
}
