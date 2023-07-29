package eu.mostserene.avogador.courseservice.controllers;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.courses.CourseController;
import eu.mostserene.avogador.courseservice.courses.CourseRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CourseControllerTests {
    private @Autowired MockMvc mvc;
    private @MockBean CourseRepository repository;
    private @MockBean UserCourseService userCourseService;
    private @MockBean UserService userService;

    private final Course course = new Course("course", "2023/2024", false);
    private final String courseJSON = "{\"name\": \"Test\", \"year\": \"2023/2024\", \"isArchived\": false}";
    private final UserDto studentUser = new UserDto(1L, "student@stud.unive.it", "Student", "1", false, false);
    private final UserDto collaboratorUser = new UserDto(2L, "collaborator@stud.unive.it", "Collaborator", "1", false, false);
    private final UserDto professorUser = new UserDto(3L, "professor@unive.it", "Professor", "1", true, false);
    private final UserCourse student = new UserCourse(studentUser, course, CourseRole.STUDENT);
    private final UserCourse collaborator = new UserCourse(studentUser, course, CourseRole.COLLABORATOR);
    private final UserCourse admin = new UserCourse(studentUser, course, CourseRole.ADMIN);

    @Test
    public void createTest_fromStudent_get401() throws Exception{
        Mockito.when(userService.getRequestUser(Mockito.any()))
                .thenReturn(studentUser);

        mvc.perform(post("/public/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseJSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void createTest_fromProfessor_get401() throws Exception{
        Mockito.when(userService.getRequestUser(Mockito.any()))
                .thenReturn(professorUser);

        mvc.perform(post("/public/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseJSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

}
