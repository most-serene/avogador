package eu.mostserene.avogador.exerciseservice.courses;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Transactional
@Service
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
    public Optional<CourseDetailDto> getUserCourseRoleDetail(UUID courseId, UUID userId) {
        CourseDetailDto courseDetailDto = new RestTemplateBuilder()
                .build()
                .getForObject("http://courses/courses/" + courseId + "/users/" + userId, CourseDetailDto.class);

        return courseDetailDto != null ? Optional.of(courseDetailDto) : Optional.empty();
    }

    @Data
    private static class UserCourseDtoList {
        private List<UserCourseDto> userCourses;

        public UserCourseDtoList() {
        }
    }
}
