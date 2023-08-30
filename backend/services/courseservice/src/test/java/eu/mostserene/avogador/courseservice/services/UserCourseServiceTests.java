package eu.mostserene.avogador.courseservice.services;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.usercourses.CourseRole;
import eu.mostserene.avogador.courseservice.usercourses.UserCourse;
import eu.mostserene.avogador.courseservice.usercourses.UserCourseRepository;
import eu.mostserene.avogador.courseservice.usercourses.UserCourseServiceImpl;
import eu.mostserene.avogador.courseservice.users.UserDto;
import eu.mostserene.avogador.courseservice.utils.ProfileManager;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

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
    private final UserDto studentUser = new UserDto(UUID.fromString("00000000-0000-0000-0000-000000000001"), "student@stud.unive.it", "Student", "1", false, false);
    private final UserDto collaboratorUser = new UserDto(UUID.fromString("00000000-0000-0000-0000-000000000002"), "collaborator@stud.unive.it", "Collaborator", "1", false, false);
    private final UserDto professorUser = new UserDto(UUID.fromString("00000000-0000-0000-0000-000000000003"), "professor@unive.it", "Professor", "1", true, false);
    private final UserDto outsiderUser = new UserDto(UUID.fromString("00000000-0000-0000-0000-000000000004"), "professor2@unive.it", "Professor2", "1", true, false);
    private final UserCourse student = new UserCourse(studentUser, course, CourseRole.STUDENT);
    private final UserCourse collaborator = new UserCourse(studentUser, course, CourseRole.COLLABORATOR);
    private final UserCourse admin = new UserCourse(studentUser, course, CourseRole.ADMIN);
    private final UserCourse newAdmin = new UserCourse(outsiderUser, course, CourseRole.ADMIN);
    private @MockBean ProfileManager profileManager;

    private @MockBean BuildProperties buildProperties;

    @Nested
    class GetUserCourse {
        @Test
        public void withClasses_returnsIsPresent() {
            when(repository.findByUserIdAndCourse_Id(argThat( (UUID l) -> l.equals(UUID.fromString("00000000-0000-0000-0000-000000000001"))), any()))
                    .thenReturn(Optional.of(student));

            var result = userCourseService.getUserCourse(studentUser.getId(), course.getId());
            assertTrue(result.isPresent());
        }

        @Test
        public void withClasses_returnsIsEmpty() {
            when(repository.findByUserIdAndCourse_Id(argThat( (UUID l) -> l.equals(UUID.fromString("00000000-0000-0000-0000-000000000004"))), any()))
                    .thenReturn(Optional.empty());

            var result = userCourseService.getUserCourse(outsiderUser.getId(), course.getId());
            assertTrue(result.isEmpty());
        }

        @Test
        public void withIds_returnsIsPresent() {
            when(repository.findByUserIdAndCourse_Id(argThat( (UUID l) -> l.equals(UUID.fromString("00000000-0000-0000-0000-000000000001"))), any()))
                    .thenReturn(Optional.of(student));

            var result = userCourseService.getUserCourse(UUID.fromString("00000000-0000-0000-0000-000000000001"), UUID.fromString("00000000-0000-0000-0000-000000000001"));
            assertTrue(result.isPresent());
        }

        @Test
        public void withIds_returnsIsEmpty() {
            when(repository.findByUserIdAndCourse_Id(argThat( (UUID l) -> l.equals(UUID.fromString("00000000-0000-0000-0000-000000000004"))), any()))
                    .thenReturn(Optional.empty());

            var result = userCourseService.getUserCourse(UUID.fromString("00000000-0000-0000-0000-000000000004"), UUID.fromString("00000000-0000-0000-0000-000000000004"));
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
            id.set(courseWithId, UUID.fromString("00000000-0000-0000-0000-000000000001"));
            var newAdmin = new UserCourse(outsiderUser, courseWithId, CourseRole.ADMIN);

            when(repository.save(argThat(uc -> uc.getCourse().getId().equals(UUID.fromString("00000000-0000-0000-0000-000000000001")) && uc.getUser().equals(UUID.fromString("00000000-0000-0000-0000-000000000004")))))
                    .thenReturn(newAdmin);

            var result = userCourseService.createAdmin(outsiderUser, courseWithId);

            assertEquals(CourseRole.ADMIN, result.getRole());
            assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000004"), result.getUser());
            assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), result.getCourse().getId());
        }
    }

    @Nested
    class CreateStudent {
        @Test
        public void createsStudent() throws Exception {
            var courseWithId = new Course("course", "2023/2024", false);
            Field id = courseWithId.getClass().getDeclaredField("id");
            id.setAccessible(true);
            id.set(courseWithId, UUID.fromString("00000000-0000-0000-0000-000000000001"));
            var newStudent = new UserCourse(outsiderUser, courseWithId, CourseRole.STUDENT);

            when(repository.save(argThat(uc -> uc.getCourse().getId().equals(UUID.fromString("00000000-0000-0000-0000-000000000001")) && uc.getUser().equals(UUID.fromString("00000000-0000-0000-0000-000000000004")))))
                    .thenReturn(newStudent);

            var result = userCourseService.createAdmin(outsiderUser, courseWithId);

            assertEquals(CourseRole.STUDENT, result.getRole());
            assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000004"), result.getUser());
            assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), result.getCourse().getId());
        }
    }

}
