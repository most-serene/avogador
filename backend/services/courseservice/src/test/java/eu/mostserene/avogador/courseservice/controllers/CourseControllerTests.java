package eu.mostserene.avogador.courseservice.controllers;

import eu.mostserene.avogador.courseservice.amqp.Sender;
import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.courses.CourseController;
import eu.mostserene.avogador.courseservice.courses.CourseRepository;
import eu.mostserene.avogador.courseservice.courses.CourseService;
import eu.mostserene.avogador.courseservice.storage.StorageService;
import eu.mostserene.avogador.courseservice.usercourses.CourseRole;
import eu.mostserene.avogador.courseservice.usercourses.UserCourse;
import eu.mostserene.avogador.courseservice.usercourses.UserCourseService;
import eu.mostserene.avogador.courseservice.users.UserDto;
import eu.mostserene.avogador.courseservice.users.UserService;
import eu.mostserene.avogador.courseservice.utils.ProfileManager;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CourseControllerTests {
    // COURSES
    private final Course course = new Course("course", "2023/2024", false);
    private final Course archivedCourse = new Course("archivedCourse", "2023/2024", true);
    private final Course updatedCourse = new Course("course2", "2023/2024", false);
    // COURSES (JSON)
    private final String courseJSON = "{\"name\": \"course\", \"year\": \"2023/2024\"}";
    private final String updatedCourseJSON = "{\"id\":\"00000000-0000-0000-0000-000000000001\", \"name\": \"course2\", \"year\": \"2023/2024\"}";
    private final String hackedCourseJSON = "{\"id\": \"00000000-0000-0000-0000-000000000001\", \"name\": \"course\", \"year\": \"2023/2024\", \"isArchived\": true}";
    // USERS
    private final UserDto studentUser = new UserDto(UUID.fromString("00000000-0000-0000-0000-000000000001"), "student@stud.unive.it", "Andy", "Bernard", false, false);
    private final UserDto collaboratorUser = new UserDto(UUID.fromString("00000000-0000-0000-0000-000000000002"), "collaborator@stud.unive.it", "Dwight", "Schrute", false, false);
    private final UserDto professorUser = new UserDto(UUID.fromString("00000000-0000-0000-0000-000000000003"), "professor@unive.it", "Michael", "Scott", true, false);
    private final UserDto superuser = new UserDto(UUID.fromString("00000000-0000-0000-0000-000000000004"), "root@unive.it", "Andy", "Bernard", false, true);
    // COURSE-USERS
    private final UserCourse superuserExternal = new UserCourse(superuser, course, CourseRole.EXTERNAL);
    private final UserCourse student = new UserCourse(studentUser, course, CourseRole.STUDENT);
    private final UserCourse collaborator = new UserCourse(studentUser, course, CourseRole.COLLABORATOR);
    private final UserCourse admin = new UserCourse(studentUser, course, CourseRole.ADMIN);
    private final UserCourse archivedAdmin = new UserCourse(professorUser, archivedCourse, CourseRole.ADMIN);
    // USER HEADERS
    private final String studentHeader = "{\"id\":\"00000000-0000-0000-0000-000000000001\", \"email\":\"student@stud.unive.it\", \"givenName\":\"Andy\", \"familyName\":\"Bernard\", \"isProfessor\":false, \"isSuperuser\":false}";
    private final String collaboratorHeader = "{\"id\":\"00000000-0000-0000-0000-000000000002\", \"email\":\"collaborator@stud.unive.it\", \"givenName\":\"Dwight\", \"familyName\":\"Schrute\", \"isProfessor\":false, \"isSuperuser\":false}";
    private final String professorHeader = "{\"id\":\"00000000-0000-0000-0000-000000000003\", \"email\":\"professor@stud.unive.it\", \"givenName\":\"Michael\", \"familyName\":\"Scott\", \"isProfessor\":true, \"isSuperuser\":false}";
    private final String superuserHeader = "{\"id\":\"00000000-0000-0000-0000-000000000004\", \"email\":\"superuser@stud.unive.it\", \"givenName\":\"Michael\", \"familyName\":\"Scott\", \"isProfessor\":false, \"isSuperuser\":true}";
    private @Autowired MockMvc mvc;
    private @MockBean CourseRepository repository;
    private @MockBean UserCourseService userCourseService;
    private @MockBean CourseService courseService;
    private @MockBean UserService userService;
    private @MockBean StorageService storageService;
    private @MockBean ProfileManager profileManager;
    private @MockBean Sender sender;
    private @MockBean BuildProperties buildProperties;

    @Nested
    class CreateCourse {
        @Test
        public void fromStudent_get403() throws Exception {
            when(courseService.createCourse(any()))
                    .thenReturn(course);

            mvc.perform(post("/public/courses")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(courseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromProfessor_get200() throws Exception {
            when(courseService.createCourse(any()))
                    .thenReturn(course);

            mvc.perform(post("/public/courses")
                            .header("User", professorHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(courseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void fromProfessor_withArchivedTrue_get200() throws Exception {
            when(courseService.createCourse(any()))
                    .thenReturn(course);

            mvc.perform(post("/public/courses")
                            .header("User", professorHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(hackedCourseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isArchived").value(false));
        }
    }

    @Nested
    class UpdateCourse {
        @Test
        public void wrongId_get400() throws Exception {
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.empty());

            mvc.perform(put("/public/courses/00000000-0000-0000-0000-000000000002")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updatedCourseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void fromOutside_get403() throws Exception {
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.empty());

            mvc.perform(put("/public/courses/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updatedCourseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromStudent_get403() throws Exception {
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.of(student));

            mvc.perform(put("/public/courses/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updatedCourseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void withArchivedTrue_get403() throws Exception {
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.of(archivedAdmin));

            mvc.perform(put("/public/courses/00000000-0000-0000-0000-000000000001")
                            .header("User", professorHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updatedCourseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromCollaborator_get200() throws Exception {
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.of(collaborator));
            when(courseService.updateCourse(any(), any()))
                    .thenReturn(updatedCourse);

            mvc.perform(put("/public/courses/00000000-0000-0000-0000-000000000001")
                            .header("User", collaboratorHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updatedCourseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void fromAdmin_get200() throws Exception {
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.of(admin));
            when(courseService.updateCourse(any(), any()))
                    .thenReturn(updatedCourse);

            mvc.perform(put("/public/courses/00000000-0000-0000-0000-000000000001")
                            .header("User", professorHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updatedCourseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class GetCourseById {
        @Test
        public void wrongCourseId_get404() throws Exception {
            when(courseService.getCourse(any()))
                    .thenReturn(Optional.empty());

            mvc.perform(get("/public/courses/00000000-0000-0000-0000-000000000000").header("User", studentHeader))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void notMember_get200_externalRole() throws Exception {
            when(courseService.getCourse(any()))
                    .thenReturn(Optional.of(course));
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.empty());

            mvc.perform(get("/public/courses/00000000-0000-0000-0000-000000000001").header("User", studentHeader))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.role").value("EXTERNAL"));
        }

        @Test
        public void fromStudent_archivedCourse_get403() throws Exception {
            when(courseService.getCourse(any()))
                    .thenReturn(Optional.of(archivedCourse));
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.of(new UserCourse(studentUser, archivedCourse, CourseRole.STUDENT)));

            mvc.perform(get("/public/courses/00000000-0000-0000-0000-000000000001").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromAdmin_archivedCourse_get200() throws Exception {
            when(courseService.getCourse(any()))
                    .thenReturn(Optional.of(archivedCourse));
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.of(archivedAdmin));
            when(courseService.getJoinCode(any()))
                    .thenReturn(Optional.of("joincode"));

            mvc.perform(get("/public/courses/00000000-0000-0000-0000-000000000001").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void isMember_get200() throws Exception {
            when(courseService.getCourse(any()))
                    .thenReturn(Optional.of(course));
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.of(student));

            mvc.perform(get("/public/courses/00000000-0000-0000-0000-000000000001").header("User", studentHeader))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void isStudent_get200_nullJoinCode() throws Exception {
            when(courseService.getCourse(any()))
                    .thenReturn(Optional.of(course));
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.of(student));

            mvc.perform(get("/public/courses/00000000-0000-0000-0000-000000000001").header("User", studentHeader))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.joinCode").isEmpty());
        }

        @Test
        public void isAdmin_get200_nullJoinCode() throws Exception {
            when(courseService.getCourse(any()))
                    .thenReturn(Optional.of(course));
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.of(admin));
            when(courseService.getJoinCode(any()))
                    .thenReturn(Optional.of("joincode"));

            mvc.perform(get("/public/courses/00000000-0000-0000-0000-000000000001").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.joinCode").isNotEmpty());
        }
    }

    @Nested
    class DeleteCourse {
        @Test
        public void fromOutside_get403() throws Exception {
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.empty());

            mvc.perform(delete("/public/courses/00000000-0000-0000-0000-000000000001").header("User", studentHeader))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromStudent_get403() throws Exception {
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.of(student));

            mvc.perform(delete("/public/courses/00000000-0000-0000-0000-000000000001").header("User", studentHeader))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromCollaborator_get403() throws Exception {
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.of(collaborator));

            mvc.perform(delete("/public/courses/00000000-0000-0000-0000-000000000001").header("User", collaboratorHeader))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromAdmin_get200() throws Exception {
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.of(admin));

            mvc.perform(delete("/public/courses/00000000-0000-0000-0000-000000000001").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class ArchiveCourse {
        @Test
        public void fromOutside_get403() throws Exception {
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.empty());

            mvc.perform(put("/public/courses/00000000-0000-0000-0000-000000000001/archive").header("User", studentHeader))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromStudent_get403() throws Exception {
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.of(student));

            mvc.perform(put("/public/courses/00000000-0000-0000-0000-000000000001/archive").header("User", studentHeader))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromCollaborator_get403() throws Exception {
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.of(collaborator));

            mvc.perform(put("/public/courses/00000000-0000-0000-0000-000000000001/archive").header("User", collaboratorHeader))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromAdmin_get200() throws Exception {
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.of(admin));

            when(courseService.archiveCourse(any()))
                    .thenReturn(archivedCourse);

            mvc.perform(put("/public/courses/00000000-0000-0000-0000-000000000001/archive").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isArchived").value(true));
        }

        @Test
        public void fromSuperuser_get200() throws Exception {
            when(userCourseService.getUserCourse(any(), any()))
                    .thenReturn(Optional.of(superuserExternal));

            when(courseService.archiveCourse(any()))
                    .thenReturn(archivedCourse);

            mvc.perform(put("/public/courses/00000000-0000-0000-0000-000000000001/archive").header("User", superuserHeader))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isArchived").value(true));
        }
    }

}
