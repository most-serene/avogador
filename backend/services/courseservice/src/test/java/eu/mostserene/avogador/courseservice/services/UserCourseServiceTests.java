package eu.mostserene.avogador.courseservice.services;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.usercourses.CourseRole;
import eu.mostserene.avogador.courseservice.usercourses.UserCourse;
import eu.mostserene.avogador.courseservice.usercourses.UserCourseRepository;
import eu.mostserene.avogador.courseservice.usercourses.UserCourseServiceImpl;
import eu.mostserene.avogador.courseservice.users.UserDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Tests for the class UserCourseServiceImpl
 * @see UserCourseServiceImpl
 * */
@ExtendWith(MockitoExtension.class)
public class UserCourseServiceTests {
    @InjectMocks
    UserCourseServiceImpl userCourseService;
    @Mock
    UserCourseRepository repository;

    private final Course course = new Course("course", "2023/2024", false);
    private final UserDto studentUser = new UserDto(1L, "student@stud.unive.it", "Student", "1", false, false);
    private final UserDto collaboratorUser = new UserDto(2L, "collaborator@stud.unive.it", "Collaborator", "1", false, false);
    private final UserDto professorUser = new UserDto(3L, "professor@unive.it", "Professor", "1", true, false);
    private final UserDto outsiderUser = new UserDto(4L, "professor2@unive.it", "Professor2", "1", true, false);
    private final UserCourse student = new UserCourse(studentUser, course, CourseRole.STUDENT);
    private final UserCourse collaborator = new UserCourse(studentUser, course, CourseRole.COLLABORATOR);
    private final UserCourse admin = new UserCourse(studentUser, course, CourseRole.ADMIN);
    private final UserCourse newAdmin = new UserCourse(outsiderUser, course, CourseRole.ADMIN);

    @Nested
    class GetUserCourse {
        @Test
        public void withClasses_returnsIsPresent() {
            when(repository.findByUserIdAndCourse_Id(argThat( (Long l) -> l == 1L), any()))
                    .thenReturn(Optional.of(student));

            var result = userCourseService.getUserCourse(studentUser, course);
            assertTrue(result.isPresent());
        }

        @Test
        public void withClasses_returnsIsEmpty() {
            when(repository.findByUserIdAndCourse_Id(argThat( (Long l) -> l == 4L), any()))
                    .thenReturn(Optional.empty());

            var result = userCourseService.getUserCourse(outsiderUser, course);
            assertTrue(result.isEmpty());
        }

        @Test
        public void withIds_returnsIsPresent() {
            when(repository.findByUserIdAndCourse_Id(argThat( (Long l) -> l == 1L), any()))
                    .thenReturn(Optional.of(student));

            var result = userCourseService.getUserCourse(1L, 1L);
            assertTrue(result.isPresent());
        }

        @Test
        public void withIds_returnsIsEmpty() {
            when(repository.findByUserIdAndCourse_Id(argThat( (Long l) -> l == 4L), any()))
                    .thenReturn(Optional.empty());

            var result = userCourseService.getUserCourse(4L, 1L);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class CreateAdmin {
        @Test
        public void createsAdmin() throws Exception {
            var courseWithId = new Course("course", "2023/2024", false);
            Field id = courseWithId.getClass().getDeclaredField("id");
            id.setAccessible(true);
            id.set(courseWithId, 1L);
            var newAdmin = new UserCourse(outsiderUser, courseWithId, CourseRole.ADMIN);

            when(repository.save(argThat(uc -> uc.getCourse().getId() == 1L && uc.getUser() == 4L)))
                    .thenReturn(newAdmin);

            var result = userCourseService.createAdmin(outsiderUser, courseWithId);

            assertEquals(CourseRole.ADMIN, result.getRole());
            assertEquals(4L, result.getUser());
            assertEquals(1L, result.getCourse().getId());
        }
    }

    @Nested
    class CreateStudent {
        @Test
        public void createsStudent() throws Exception {
            var courseWithId = new Course("course", "2023/2024", false);
            Field id = courseWithId.getClass().getDeclaredField("id");
            id.setAccessible(true);
            id.set(courseWithId, 1L);
            var newStudent = new UserCourse(outsiderUser, courseWithId, CourseRole.STUDENT);

            when(repository.save(argThat(uc -> uc.getCourse().getId() == 1L && uc.getUser() == 4L)))
                    .thenReturn(newStudent);

            var result = userCourseService.createAdmin(outsiderUser, courseWithId);

            assertEquals(CourseRole.STUDENT, result.getRole());
            assertEquals(4L, result.getUser());
            assertEquals(1L, result.getCourse().getId());
        }
    }

}
