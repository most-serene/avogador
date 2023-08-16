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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    private final UserDto student1User = new UserDto(1L, "student1@stud.unive.it", "Andy", "Bernard", false, false);
    private final UserDto student2User = new UserDto(4L, "student2@stud.unive.it", "Angela", "Martin", false, false);
    private final UserDto collaboratorUser = new UserDto(2L, "collaborator@stud.unive.it", "Dwight", "Schrute", false, false);
    private final UserDto professorUser = new UserDto(3L, "professor@unive.it", "Michael", "Scott", true, false);
    private final UserCourse student1 = new UserCourse(student1User, course, CourseRole.STUDENT);
    private final UserCourse student2 = new UserCourse(student2User, course, CourseRole.STUDENT);
    private final UserCourse promotedStudent = new UserCourse(student1User, course, CourseRole.COLLABORATOR);
    private final UserCourse collaborator = new UserCourse(student1User, course, CourseRole.COLLABORATOR);
    private final UserCourse demotedCollaborator = new UserCourse(student1User, course, CourseRole.STUDENT);
    private final UserCourse admin = new UserCourse(student1User, course, CourseRole.ADMIN);
    private final UserCourse archivedAdmin = new UserCourse(professorUser, archivedCourse, CourseRole.ADMIN);
    private final String student1Header = "{\"id\":1, \"email\":\"student1@stud.unive.it\", \"givenName\":\"Andy\", \"familyName\":\"Bernard\", \"isProfessor\":false, \"isSuperuser\":false}";
    private final String student2Header = "{\"id\":4, \"email\":\"studen2@stud.unive.it\", \"givenName\":\"Angela\", \"familyName\":\"Martin\", \"isProfessor\":false, \"isSuperuser\":false}";
    private final String collaboratorHeader = "{\"id\":2, \"email\":\"collaborator@stud.unive.it\", \"givenName\":\"Dwight\", \"familyName\":\"Schrute\", \"isProfessor\":false, \"isSuperuser\":false}";
    private final String professorHeader = "{\"id\":3, \"email\":\"professor@stud.unive.it\", \"givenName\":\"Michael\", \"familyName\":\"Scott\", \"isProfessor\":true, \"isSuperuser\":false}";



    @Nested
    class JoinCourse{
        @Test
        public void wrongCourseId_get404() throws Exception{
            when(courseService.getCourse(anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(put("/public/courses/11/join/joinCode").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void wrongJoinCode_get403() throws Exception{
            when(courseService.getCourse(anyLong()))
                    .thenReturn(Optional.of(course));
            when(courseService.getJoinCode(anyLong()))
                    .thenReturn(Optional.of("7ba32aca07cc92001d74537d5ff775343390210f6812450d844bb9a24598c3ff"));

            mvc.perform(put("/public/courses/1/join/joinCode").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("Wrong join code"));
        }

        @Test
        public void archivedCourse_get403() throws Exception{
            when(courseService.getCourse(anyLong()))
                    .thenReturn(Optional.of(archivedCourse));

            mvc.perform(put("/public/courses/1/join/joinCode").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("This course is archived"));
        }

        @Test
        public void alreadyPresent_get200() throws Exception{
            when(courseService.getCourse(anyLong()))
                    .thenReturn(Optional.of(course));
            when(courseService.getJoinCode(anyLong()))
                    .thenReturn(Optional.of("7ba32aca07cc92001d74537d5ff775343390210f6812450d844bb9a24598c3ff"));
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(admin));

            mvc.perform(put("/public/courses/1/join/7ba32aca07cc92001d74537d5ff775343390210f6812450d844bb9a24598c3ff").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.role").value("ADMIN"));
        }

        @Test
        public void everythingRight_get200() throws Exception{
            when(courseService.getCourse(anyLong()))
                    .thenReturn(Optional.of(course));
            when(courseService.getJoinCode(anyLong()))
                    .thenReturn(Optional.of("7ba32aca07cc92001d74537d5ff775343390210f6812450d844bb9a24598c3ff"));
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(put("/public/courses/1/join/7ba32aca07cc92001d74537d5ff775343390210f6812450d844bb9a24598c3ff").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class PromoteToCollaborator {
        @Test
        public void fromOutside_get403() throws Exception{
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(put("/public/courses/1/collaborators/1").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You cannot promote users in this course"));
        }

        @Test
        public void fromStudent_get403() throws Exception{
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(student1));


            mvc.perform(put("/public/courses/1/collaborators/1").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You cannot promote users in this course"));
        }

        @Test
        public void promotedUserIsNotMember_get400() throws Exception{
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, professorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(admin));
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, 1L)), anyLong()))
                    .thenReturn(Optional.empty());


            mvc.perform(put("/public/courses/1/collaborators/1").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason("User is not part of the course"));
        }

        @Test
        public void archivedCourse_get403() throws Exception{
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(archivedAdmin));

            mvc.perform(put("/public/courses/1/collaborators/1").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("This course is archived"));
        }

        @Test
        public void everythingRight_get200() throws Exception{
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, professorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(admin));
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, 1L)), anyLong()))
                    .thenReturn(Optional.of(student1));
            when(userCourseService.promoteToCollaborator(any()))
                    .thenReturn(promotedStudent);


            mvc.perform(put("/public/courses/1/collaborators/1").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.role").value("COLLABORATOR"));
        }
    }

    @Nested
    class DemoteToCollaborator {
        @Test
        public void fromOutside_get403() throws Exception{
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(put("/public/courses/1/students/1").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You cannot demote users in this course"));
        }

        @Test
        public void fromStudent_get403() throws Exception{
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(student1));


            mvc.perform(put("/public/courses/1/students/1").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You cannot demote users in this course"));
        }

        @Test
        public void promotedUserIsNotMember_get400() throws Exception{
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, professorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(admin));
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, 1L)), anyLong()))
                    .thenReturn(Optional.empty());


            mvc.perform(put("/public/courses/1/students/1").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason("User is not part of the course"));
        }

        @Test
        public void archivedCourse_get403() throws Exception{
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(archivedAdmin));

            mvc.perform(put("/public/courses/1/students/1").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("This course is archived"));
        }

        @Test
        public void everythingRight_get200() throws Exception{
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, professorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(admin));
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, 1L)), anyLong()))
                    .thenReturn(Optional.of(collaborator));
            when(userCourseService.demoteToStudent(any()))
                    .thenReturn(demotedCollaborator);


            mvc.perform(put("/public/courses/1/students/1").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.role").value("STUDENT"));
        }
    }

    @Nested
    class GetCoursesByUser {
        @Test
        public void idMismatch_get400() throws Exception{
            mvc.perform(get("/public/courses/users/2").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason("You can't spy on others!"));
        }

        @Test
        public void everythingRight_get200() throws Exception{
            when(userCourseService.getCoursesByUserId(anyLong(), argThat(isArc -> Objects.equals(isArc, false))))
                    .thenReturn(List.of());

            mvc.perform(get("/public/courses/users/1").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class GetUsersByCourse {
        @Test
        public void wrongCourseId_get403() throws Exception{
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(get("/public/courses/2/users").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You cannot see the participants of this course"));
        }

        @Test
        public void fromStudent_get403() throws Exception{
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(student1));

            mvc.perform(get("/public/courses/2/users").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You cannot see the participants of this course"));
        }

        @Test
        public void fromCollaborator_get200() throws Exception{
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(collaborator));
            when(userCourseService.getUsersByCourseId(anyLong()))
                    .thenReturn(List.of());

            mvc.perform(get("/public/courses/2/users").header("User", collaboratorHeader))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void fromAdmin_get200() throws Exception{
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(admin));
            when(userCourseService.getUsersByCourseId(anyLong()))
                    .thenReturn(List.of());

            mvc.perform(get("/public/courses/1/users").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class LeaveCourse {
        @Test
        public void notMember_get403() throws Exception{
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(delete("/public/courses/1/users/1").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You are not part of this course"));
        }

        @Test
        public void archivedCourse_get403() throws Exception{
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(archivedAdmin));

            mvc.perform(delete("/public/courses/1/users/1").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("This course is archived"));
        }

        @Test
        public void adminSelfDelete_get403() throws Exception{
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(admin));

            mvc.perform(delete("/public/courses/1/users/3").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("An admin cannot remove themselves"));
        }

        @Test
        public void studentDeleteAdmin_get403() throws Exception{
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, student1User.getId())), anyLong()))
                    .thenReturn(Optional.of(student1));
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, professorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(admin));

            mvc.perform(delete("/public/courses/1/users/3").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You cannot remove this user"));
        }

        @Test
        public void studentDeleteStudent_get403() throws Exception{
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, student1User.getId())), anyLong()))
                    .thenReturn(Optional.of(student1));
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, student2User.getId())), anyLong()))
                    .thenReturn(Optional.of(student2));

            mvc.perform(delete("/public/courses/1/users/4").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You cannot remove this user"));
        }

        @Test
        public void collaboratorDeleteStudent_get200() throws Exception{
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, collaboratorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(collaborator));
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, student1User.getId())), anyLong()))
                    .thenReturn(Optional.of(student1));

            mvc.perform(delete("/public/courses/1/users/1").header("User", collaboratorHeader))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void adminDeleteCollaborator_get200() throws Exception{
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, professorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(admin));
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, collaboratorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(collaborator));

            mvc.perform(delete("/public/courses/1/users/2").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void studentSelfDelete_get200() throws Exception{
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, student1User.getId())), anyLong()))
                    .thenReturn(Optional.of(student1));

            mvc.perform(delete("/public/courses/1/users/1").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void collaboratorSelfDelete_get200() throws Exception{
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, collaboratorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(collaborator));

            mvc.perform(delete("/public/courses/1/users/2").header("User", collaboratorHeader))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }
}
