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
    private final UserDto studentUser = new UserDto(1L, "student@stud.unive.it", "Student", "1", false, false);
    private final UserDto secondStudentUser = new UserDto(4L, "student@stud.unive.it", "Student", "1", false, false);
    private final UserDto collaboratorUser = new UserDto(2L, "collaborator@stud.unive.it", "Collaborator", "1", false, false);
    private final UserDto professorUser = new UserDto(3L, "professor@unive.it", "Professor", "1", true, false);
    private final UserCourse student = new UserCourse(studentUser, course, CourseRole.STUDENT);
    private final UserCourse secondStudent = new UserCourse(secondStudentUser, course, CourseRole.STUDENT);
    private final UserCourse promotedStudent = new UserCourse(studentUser, course, CourseRole.COLLABORATOR);
    private final UserCourse collaborator = new UserCourse(studentUser, course, CourseRole.COLLABORATOR);
    private final UserCourse demotedCollaborator = new UserCourse(studentUser, course, CourseRole.STUDENT);
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
        public void alreadyPresent_get200() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(professorUser);
            when(courseService.getCourse(anyLong()))
                    .thenReturn(Optional.of(course));
            when(courseService.getJoinCode(anyLong()))
                    .thenReturn(Optional.of("7ba32aca07cc92001d74537d5ff775343390210f6812450d844bb9a24598c3ff"));
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(admin));

            mvc.perform(put("/public/courses/1/join/7ba32aca07cc92001d74537d5ff775343390210f6812450d844bb9a24598c3ff"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.role").value("ADMIN"));
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

    @Nested
    class PromoteToCollaborator {
        @Test
        public void fromOutside_get403() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(studentUser);
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(put("/public/courses/1/collaborators/1"))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You cannot promote users in this course"));
        }

        @Test
        public void fromStudent_get403() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(studentUser);
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(student));


            mvc.perform(put("/public/courses/1/collaborators/1"))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You cannot promote users in this course"));
        }

        @Test
        public void promotedUserIsNotMember_get403() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(professorUser);
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, professorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(admin));
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, 1L)), anyLong()))
                    .thenReturn(Optional.empty());


            mvc.perform(put("/public/courses/1/collaborators/1"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason("User is not part of the course"));
        }

        @Test
        public void everythingRight_get200() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(professorUser);
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, professorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(admin));
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, 1L)), anyLong()))
                    .thenReturn(Optional.of(student));
            when(userCourseService.promoteToCollaborator(any()))
                    .thenReturn(promotedStudent);


            mvc.perform(put("/public/courses/1/collaborators/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.role").value("COLLABORATOR"));
        }
    }

    @Nested
    class DemoteToCollaborator {
        @Test
        public void fromOutside_get403() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(studentUser);
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(put("/public/courses/1/students/1"))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You cannot demote users in this course"));
        }

        @Test
        public void fromStudent_get403() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(studentUser);
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(student));


            mvc.perform(put("/public/courses/1/students/1"))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You cannot demote users in this course"));
        }

        @Test
        public void promotedUserIsNotMember_get403() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(professorUser);
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, professorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(admin));
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, 1L)), anyLong()))
                    .thenReturn(Optional.empty());


            mvc.perform(put("/public/courses/1/students/1"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason("User is not part of the course"));
        }

        @Test
        public void everythingRight_get200() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(professorUser);
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, professorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(admin));
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, 1L)), anyLong()))
                    .thenReturn(Optional.of(collaborator));
            when(userCourseService.demoteToStudent(any()))
                    .thenReturn(demotedCollaborator);


            mvc.perform(put("/public/courses/1/students/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.role").value("STUDENT"));
        }
    }

    @Nested
    class GetCoursesByUser {
        @Test
        public void idMismatch_get400() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(studentUser);

            mvc.perform(get("/public/courses/users/2"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason("You can't spy on others!"));
        }

        @Test
        public void everythingRight_get200() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(studentUser);
            when(userCourseService.getCoursesByUserId(anyLong()))
                    .thenReturn(List.of());

            mvc.perform(get("/public/courses/users/1"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class GetUsersByCourse {
        @Test
        public void wrongCourseId_get403() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(studentUser);
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(get("/public/courses/2/users"))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You are not part of this course"));
        }

        @Test
        public void fromStudent_get403() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(studentUser);
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(student));

            mvc.perform(get("/public/courses/2/users"))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You cannot see the participants of this course"));
        }

        @Test
        public void fromCollaborator_get200() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(collaboratorUser);
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(collaborator));
            when(userCourseService.getUsersByCourseId(anyLong()))
                    .thenReturn(List.of());

            mvc.perform(get("/public/courses/2/users"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void fromAdmin_get200() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(professorUser);
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(admin));
            when(userCourseService.getUsersByCourseId(anyLong()))
                    .thenReturn(List.of());

            mvc.perform(get("/public/courses/1/users"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class LeaveCourse {
        @Test
        public void notMember_get403() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(studentUser);
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(delete("/public/courses/1/users/1"))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You are not part of this course"));
        }

        @Test
        public void adminSelfDelete_get403() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(professorUser);
            when(userCourseService.getUserCourse(anyLong(), anyLong()))
                    .thenReturn(Optional.of(admin));

            mvc.perform(delete("/public/courses/1/users/3"))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("An admin cannot remove themselves"));
        }

        @Test
        public void studentDeleteAdmin_get403() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(studentUser);
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, studentUser.getId())), anyLong()))
                    .thenReturn(Optional.of(student));
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, professorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(admin));

            mvc.perform(delete("/public/courses/1/users/3"))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You cannot remove this user"));
        }

        @Test
        public void studentDeleteStudent_get403() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(studentUser);
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, studentUser.getId())), anyLong()))
                    .thenReturn(Optional.of(student));
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, secondStudentUser.getId())), anyLong()))
                    .thenReturn(Optional.of(secondStudent));

            mvc.perform(delete("/public/courses/1/users/4"))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("You cannot remove this user"));
        }

        @Test
        public void collaboratorDeleteStudent_get200() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(collaboratorUser);
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, collaboratorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(collaborator));
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, studentUser.getId())), anyLong()))
                    .thenReturn(Optional.of(student));

            mvc.perform(delete("/public/courses/1/users/1"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void adminDeleteCollaborator_get200() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(professorUser);
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, professorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(admin));
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, collaboratorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(collaborator));

            mvc.perform(delete("/public/courses/1/users/2"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void studentSelfDelete_get200() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(studentUser);
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, studentUser.getId())), anyLong()))
                    .thenReturn(Optional.of(student));

            mvc.perform(delete("/public/courses/1/users/1"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void collaboratorSelfDelete_get200() throws Exception{
            when(userService.getRequestUser(any()))
                    .thenReturn(collaboratorUser);
            when(userCourseService.getUserCourse(argThat(id -> Objects.equals(id, collaboratorUser.getId())), anyLong()))
                    .thenReturn(Optional.of(collaborator));

            mvc.perform(delete("/public/courses/1/users/2"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }
}
