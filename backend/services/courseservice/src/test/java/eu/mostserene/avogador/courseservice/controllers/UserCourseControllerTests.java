package eu.mostserene.avogador.courseservice.controllers;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.courses.CourseService;
import eu.mostserene.avogador.courseservice.filesystem.FileSystemService;
import eu.mostserene.avogador.courseservice.usercourses.*;
import eu.mostserene.avogador.courseservice.users.UserDto;
import eu.mostserene.avogador.courseservice.users.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserCourseController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserCourseControllerTests {
    private @Autowired MockMvc mvc;
    private @MockBean UserCourseRepository repository;
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
    class JoinCourse{
        @Test
        public void wrongCourseId_get400() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(studentUser);
            when(courseService.getCourse(anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(put("/public/courses/11/join/joinCode"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void wrongJoinCode_get403() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(studentUser);
            when(courseService.getCourse(anyLong()))
                    .thenReturn(Optional.of(course));
            when(courseService.getJoinCode(anyLong()))
                    .thenReturn(Optional.of("7ba32aca07cc92001d74537d5ff775343390210f6812450d844bb9a24598c3ff"));

            mvc.perform(put("/public/courses/1/join/joinCode"))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("Wrong join code"));
        }

        @Test
        public void alreadyPresent_get403() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(studentUser);
            when(courseService.getCourse(anyLong()))
                    .thenReturn(Optional.of(course));
            when(courseService.getJoinCode(anyLong()))
                    .thenReturn(Optional.of("7ba32aca07cc92001d74537d5ff775343390210f6812450d844bb9a24598c3ff"));
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(student));

            mvc.perform(put("/public/courses/1/join/7ba32aca07cc92001d74537d5ff775343390210f6812450d844bb9a24598c3ff"))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You are already part of the course"));
        }

        @Test
        public void everythingRight_get200() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(studentUser);
            when(courseService.getCourse(anyLong()))
                    .thenReturn(Optional.of(course));
            when(courseService.getJoinCode(anyLong()))
                    .thenReturn(Optional.of("7ba32aca07cc92001d74537d5ff775343390210f6812450d844bb9a24598c3ff"));
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(put("/public/courses/1/join/7ba32aca07cc92001d74537d5ff775343390210f6812450d844bb9a24598c3ff"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }
}
