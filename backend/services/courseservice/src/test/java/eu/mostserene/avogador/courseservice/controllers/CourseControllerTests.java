package eu.mostserene.avogador.courseservice.controllers;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.courses.CourseController;
import eu.mostserene.avogador.courseservice.courses.CourseRepository;
import eu.mostserene.avogador.courseservice.courses.CourseService;
import eu.mostserene.avogador.courseservice.usercourses.CourseRole;
import eu.mostserene.avogador.courseservice.usercourses.UserCourse;
import eu.mostserene.avogador.courseservice.usercourses.UserCourseService;
import eu.mostserene.avogador.courseservice.users.UserDto;
import eu.mostserene.avogador.courseservice.users.UserService;
import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CourseControllerTests {
    private @Autowired MockMvc mvc;
    private @MockBean CourseRepository repository;
    private @MockBean UserCourseService userCourseService;
    private @MockBean CourseService courseService;
    private @MockBean UserService userService;

    private final Course course = new Course("course", "2023/2024", false);
    private final String courseJSON = "{\"name\": \"Test\", \"year\": \"2023/2024\"}";
    private final UserDto studentUser = new UserDto(1L, "student@stud.unive.it", "Student", "1", false, false);
    private final UserDto collaboratorUser = new UserDto(2L, "collaborator@stud.unive.it", "Collaborator", "1", false, false);
    private final UserDto professorUser = new UserDto(3L, "professor@unive.it", "Professor", "1", true, false);
    private final UserCourse student = new UserCourse(studentUser, course, CourseRole.STUDENT);
    private final UserCourse collaborator = new UserCourse(studentUser, course, CourseRole.COLLABORATOR);
    private final UserCourse admin = new UserCourse(studentUser, course, CourseRole.ADMIN);

    @Test
    public void createCourse_fromStudent_get401() throws Exception{
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
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void createCourse_fromProfessor_get200() throws Exception{
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
    public void updateCourse_fromStudent_get401() throws Exception{
        Mockito
            .when(userService.getRequestUser(Mockito.any()))
            .thenReturn(studentUser);
        Mockito
            .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(Optional.of(student));
        Mockito
            .when(courseService.updateCourse(Mockito.anyLong(), Mockito.any()))
            .thenReturn(course);

        mvc.perform(put("/public/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseJSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateCourse_fromCollaborator_get200() throws Exception{
        Mockito
            .when(userService.getRequestUser(Mockito.any()))
            .thenReturn(collaboratorUser);
        Mockito
            .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(Optional.of(collaborator));
        Mockito
            .when(courseService.updateCourse(Mockito.anyLong(), Mockito.any()))
            .thenReturn(course);

        mvc.perform(put("/public/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseJSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void updateCourse_fromAdmin_get200() throws Exception{
        Mockito
            .when(userService.getRequestUser(Mockito.any()))
            .thenReturn(professorUser);
        Mockito
            .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(Optional.of(admin));
        Mockito
            .when(courseService.updateCourse(Mockito.anyLong(), Mockito.any()))
            .thenReturn(course);

        mvc.perform(put("/public/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseJSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getCourseById_notMember_get401() throws Exception{
        Mockito
            .when(userService.getRequestUser(Mockito.any()))
            .thenReturn(studentUser);
        Mockito
            .when(userCourseService.getUserCourse(Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(Optional.empty());

        mvc.perform(get("/public/courses/1"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getCourseById_isMember_get200() throws Exception{
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
