package eu.mostserene.avogador.courseservice.controllers;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.courses.CourseController;
import eu.mostserene.avogador.courseservice.courses.CourseRepository;
import eu.mostserene.avogador.courseservice.courses.CourseService;
import eu.mostserene.avogador.courseservice.filesystem.FileSystemService;
import eu.mostserene.avogador.courseservice.usercourses.CourseRole;
import eu.mostserene.avogador.courseservice.usercourses.UserCourse;
import eu.mostserene.avogador.courseservice.usercourses.UserCourseService;
import eu.mostserene.avogador.courseservice.users.UserDto;
import eu.mostserene.avogador.courseservice.users.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.swing.text.html.Option;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CourseControllerTests {
    private @Autowired MockMvc mvc;
    private @MockBean CourseRepository repository;
    private @MockBean UserCourseService userCourseService;
    private @MockBean CourseService courseService;
    private @MockBean UserService userService;
    private @MockBean FileSystemService fileSystemService;

    private final Course course = new Course("course", "2023/2024", false);
    private final Course archivedCourse = new Course("archivedCourse", "2023/2024", true);
    private final Course updatedCourse = new Course("course2", "2023/2024", false);
    private final String courseJSON = "{\"name\": \"course\", \"year\": \"2023/2024\"}";
    private final String updatedCourseJSON = "{\"id\": 1, \"name\": \"course2\", \"year\": \"2023/2024\"}";
    private final String hackedCourseJSON = "{\"id\": 1, \"name\": \"course\", \"year\": \"2023/2024\", \"isArchived\": true}";
    private final UserDto studentUser = new UserDto(1L, "student@stud.unive.it", "Student", "1", false, false);
    private final UserDto collaboratorUser = new UserDto(2L, "collaborator@stud.unive.it", "Collaborator", "1", false, false);
    private final UserDto professorUser = new UserDto(3L, "professor@unive.it", "Professor", "1", true, false);
    private final UserCourse student = new UserCourse(studentUser, course, CourseRole.STUDENT);
    private final UserCourse collaborator = new UserCourse(studentUser, course, CourseRole.COLLABORATOR);
    private final UserCourse admin = new UserCourse(studentUser, course, CourseRole.ADMIN);
    private final UserCourse archivedAdmin = new UserCourse(professorUser, archivedCourse, CourseRole.ADMIN);

    @Nested
    class CreateCourse{
        @Test
        public void fromStudent_get403() throws Exception{
            Mockito
                .when(userService.getRequestUser(Mockito.any()))
                .thenReturn(studentUser);
            Mockito
                .when(courseService.createCourse(Mockito.any()))
                .thenReturn(course);

            mvc.perform(post("/public/courses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(courseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromProfessor_get200() throws Exception{
            Mockito
                .when(userService.getRequestUser(Mockito.any()))
                .thenReturn(professorUser);
            Mockito
                .when(courseService.createCourse(Mockito.any()))
                .thenReturn(course);

            mvc.perform(post("/public/courses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(courseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void fromProfessor_withArchivedTrue_get200() throws Exception{
            Mockito
                    .when(userService.getRequestUser(Mockito.any()))
                    .thenReturn(professorUser);
            Mockito
                    .when(courseService.createCourse(Mockito.any()))
                    .thenReturn(course);

            mvc.perform(post("/public/courses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(hackedCourseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isArchived").value(false));
        }
    }

    @Nested
    class UpdateCourse{
        @Test
        public void wrongId_get400() throws Exception{
            Mockito
                .when(userService.getRequestUser(Mockito.any()))
                .thenReturn(studentUser);
            Mockito
                .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.empty());

            mvc.perform(put("/public/courses/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updatedCourseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void fromOutside_get403() throws Exception{
            Mockito
                .when(userService.getRequestUser(Mockito.any()))
                .thenReturn(studentUser);
            Mockito
                .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.empty());

            mvc.perform(put("/public/courses/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updatedCourseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromStudent_get403() throws Exception{
            Mockito
                .when(userService.getRequestUser(Mockito.any()))
                .thenReturn(studentUser);
            Mockito
                .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(student));

            mvc.perform(put("/public/courses/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updatedCourseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void withArchivedTrue_get403() throws Exception{
            Mockito
                    .when(userService.getRequestUser(Mockito.any()))
                    .thenReturn(professorUser);
            Mockito
                    .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(Optional.of(archivedAdmin));

            mvc.perform(put("/public/courses/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updatedCourseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromCollaborator_get200() throws Exception{
            Mockito
                .when(userService.getRequestUser(Mockito.any()))
                .thenReturn(collaboratorUser);
            Mockito
                .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(collaborator));
            Mockito
                .when(courseService.updateCourse(Mockito.anyLong(), Mockito.any()))
                .thenReturn(updatedCourse);

            mvc.perform(put("/public/courses/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updatedCourseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void fromAdmin_get200() throws Exception{
            Mockito
                .when(userService.getRequestUser(Mockito.any()))
                .thenReturn(professorUser);
            Mockito
                .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(admin));
            Mockito
                .when(courseService.updateCourse(Mockito.anyLong(), Mockito.any()))
                .thenReturn(updatedCourse);

            mvc.perform(put("/public/courses/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updatedCourseJSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class GetCourseById{

        @Test
        public void notMember_get403() throws Exception{
            Mockito
                .when(userService.getRequestUser(Mockito.any()))
                .thenReturn(studentUser);
            Mockito
                .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.empty());

            mvc.perform(get("/public/courses/1"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromStudent_archivedCourse_get403() throws Exception{
            Mockito
                    .when(userService.getRequestUser(Mockito.any()))
                    .thenReturn(professorUser);
            Mockito
                    .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(Optional.of(new UserCourse(studentUser, archivedCourse, CourseRole.STUDENT)));

            mvc.perform(get("/public/courses/1"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromAdmin_archivedCourse_get200() throws Exception{
            Mockito
                    .when(userService.getRequestUser(Mockito.any()))
                    .thenReturn(professorUser);
            Mockito
                    .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(Optional.of(archivedAdmin));

            mvc.perform(get("/public/courses/1"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void isMember_get200() throws Exception{
            Mockito
                .when(userService.getRequestUser(Mockito.any()))
                .thenReturn(studentUser);
            Mockito
                .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(student));

            mvc.perform(get("/public/courses/1"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class DeleteCourse{
        @Test
        public void fromOutside_get403() throws Exception{
            Mockito
                    .when(userService.getRequestUser(Mockito.any()))
                    .thenReturn(studentUser);
            Mockito
                    .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(delete("/public/courses/1"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromStudent_get403() throws Exception{
            Mockito
                    .when(userService.getRequestUser(Mockito.any()))
                    .thenReturn(studentUser);
            Mockito
                    .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(Optional.of(student));

            mvc.perform(delete("/public/courses/1"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromCollaborator_get403() throws Exception{
            Mockito
                    .when(userService.getRequestUser(Mockito.any()))
                    .thenReturn(collaboratorUser);
            Mockito
                    .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(Optional.of(collaborator));

            mvc.perform(delete("/public/courses/1"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromAdmin_get200() throws Exception{
            Mockito
                    .when(userService.getRequestUser(Mockito.any()))
                    .thenReturn(professorUser);
            Mockito
                    .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(Optional.of(admin));

            mvc.perform(delete("/public/courses/1"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class ArchiveCourse {
        @Test
        public void fromOutside_get403() throws Exception{
            Mockito
                    .when(userService.getRequestUser(Mockito.any()))
                    .thenReturn(studentUser);
            Mockito
                    .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(put("/public/courses/1/archive"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromStudent_get403() throws Exception{
            Mockito
                    .when(userService.getRequestUser(Mockito.any()))
                    .thenReturn(studentUser);
            Mockito
                    .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(Optional.of(student));

            mvc.perform(put("/public/courses/1/archive"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromCollaborator_get403() throws Exception{
            Mockito
                    .when(userService.getRequestUser(Mockito.any()))
                    .thenReturn(collaboratorUser);
            Mockito
                    .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(Optional.of(collaborator));

            mvc.perform(put("/public/courses/1/archive"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromAdmin_get200() throws Exception{
            Mockito
                    .when(userService.getRequestUser(Mockito.any()))
                    .thenReturn(professorUser);
            Mockito
                    .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(Optional.of(admin));

            mvc.perform(put("/public/courses/1/archive"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isArchived").value(true));
        }
    }

}
