package eu.mostserene.avogador.courseservice.services;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.courses.CourseRepository;
import eu.mostserene.avogador.courseservice.courses.CourseServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTests {

    @InjectMocks
    CourseServiceImpl courseService;
    @Mock
    CourseRepository repository;

    private final Course course = new Course("course", "2023/2024", false);
    private final Course hackedCourse = new Course("course", "2023/2024", true);

    @Nested
    class CreateCourse {
        @Test
        void isArchivedSetFalse() {
            when(repository.save(argThat(course -> !course.getIsArchived())))
                    .thenReturn(course);

            var result = courseService.createCourse(course);
            assertFalse(result.getIsArchived());
        }

        @Test
        void isArchivedSetTrue() {
            when(repository.save(argThat(course -> !course.getIsArchived())))
                    .thenReturn(course);

            var result = courseService.createCourse(hackedCourse);
            assertFalse(result.getIsArchived());
        }
    }

    @Nested
    class UpdateCourse {
        @Test
        void isArchivedSetFalse(){
            when(repository.save(argThat(course -> !course.getIsArchived())))
                    .thenReturn(course);

            var result = courseService.updateCourse(1L, course);
            assertFalse(result.getIsArchived());
        }

        @Test
        void isArchivedSetTrue()  {
            when(repository.save(argThat(course -> !course.getIsArchived())))
                .thenReturn(course);

            var result = courseService.updateCourse(1L, hackedCourse);
            assertFalse(result.getIsArchived());
        }
    }
    
    @Nested
    class GetById {
        @Test
        void courseExists() throws Exception {
            var courseWithId = new Course("course", "2023/2024", false);
            Field id = courseWithId.getClass().getDeclaredField("id");
            id.setAccessible(true);
            id.set(courseWithId, 1L);

            when(repository.findById(anyLong()))
                    .thenReturn(Optional.of(courseWithId));

            var result = courseService.getCourse(1L);

            assertTrue(result.isPresent());
            assertEquals(1L, result.get().getId());
        }

        @Test
        void courseNotExists() throws Exception {
            when(repository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            var result = courseService.getCourse(2L);

            assertTrue(result.isEmpty());
        }
    }

}
