package eu.mostserene.avogador.exerciseservice.controllers;

import eu.mostserene.avogador.exerciseservice.courses.CourseDetailDto;
import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.practices.Practice;
import eu.mostserene.avogador.exerciseservice.trials.ProgrammingLanguage;
import eu.mostserene.avogador.exerciseservice.trials.TrialService;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.users.UserService;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrial;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialController;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialService;
import eu.mostserene.avogador.exerciseservice.utils.ProfileManager;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserTrialController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserTrialControllerTests {
    private final Practice practice = new Practice(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Practice One",
            true, true, ProgrammingLanguage.JAVA, Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private final Practice privatePractice = new Practice(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Practice One",
            true, false, ProgrammingLanguage.JAVA, Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private final Practice notVisibilePractice = new Practice(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Practice One",
            false, true, ProgrammingLanguage.JAVA, Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private final Practice privateNotVisisblePractice = new Practice(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Practice One",
            false, false, ProgrammingLanguage.JAVA, Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private final String studentHeader = "{\"id\":\"00000000-0000-0000-0000-000000000001\", \"email\":\"student@stud.unive.it\", \"givenName\":\"Andy\", \"familyName\":\"Bernard\", \"isProfessor\":false, \"isSuperuser\":false}";
    private final String superUserHeader = "{\"id\":\"00000000-0000-0000-0000-000000000001\", \"email\":\"superuser@stud.unive.it\", \"givenName\":\"Andy\", \"familyName\":\"Bernard\", \"isProfessor\":false, \"isSuperuser\":true}";
    private final UserTrial userTrial1 = new UserTrial(UUID.fromString("00000000-0000-0000-0000-000000000001"), practice, false);
    private final UserDto studentUser = new UserDto(UUID.fromString("00000000-0000-0000-0000-000000000001"), "student@stud.unive.it", "Andy", "Bernard", false, false);

    private final CourseDetailDto courseDetailDtoExternal = new CourseDetailDto(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "Course Name",
            "2023/2024",
            false,
            CourseRole.EXTERNAL
    );

    private final CourseDetailDto courseDetailDtoStudent = new CourseDetailDto(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "Course Name",
            "2023/2024",
            false,
            CourseRole.STUDENT
    );

    private final CourseDetailDto courseDetailDtoCollaborator = new CourseDetailDto(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "Course Name",
            "2023/2024",
            false,
            CourseRole.COLLABORATOR
    );

    private final CourseDetailDto courseDetailDtoAdmin = new CourseDetailDto(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "Course Name",
            "2023/2024",
            false,
            CourseRole.ADMIN
    );

    private @Autowired MockMvc mvc;
    private @MockBean BuildProperties buildProperties;
    private @MockBean ProfileManager profileManager;
    private @MockBean UserTrialService userTrialService;
    private @MockBean UserService userService;
    private @MockBean UserCourseService userCourseService;
    private @MockBean TrialService trialService;

    @Nested
    class GetUsersFromTrial {
        @Test
        public void wrongTrialId_get404() throws Exception {
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.empty());

            mvc.perform(get("/public/trials/00000000-0000-0000-0000-000000000001/users")
                            .header("User", studentHeader))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void emptyUserRole_get403() throws Exception {
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRoleDetail(any(), any()))
                    .thenReturn(Optional.empty());

            mvc.perform(get("/public/trials/00000000-0000-0000-0000-000000000001/users")
                            .header("User", studentHeader))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void userIsStudent_get403() throws Exception {
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRoleDetail(any(), any()))
                    .thenReturn(Optional.of(courseDetailDtoStudent));

            mvc.perform(get("/public/trials/00000000-0000-0000-0000-000000000001/users")
                            .header("User", studentHeader))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void userIsCollaborator_get200() throws Exception {
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRoleDetail(any(), any()))
                    .thenReturn(Optional.of(courseDetailDtoCollaborator));
            when(userTrialService.getUsersFromTrial(any()))
                    .thenReturn(List.of(userTrial1));
            when(userService.getUsersFromIdList(anyList()))
                    .thenReturn(List.of(studentUser));

            mvc.perform(get("/public/trials/00000000-0000-0000-0000-000000000001/users")
                            .header("User", studentHeader))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void userIsAdmin_get200() throws Exception {
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRoleDetail(any(), any()))
                    .thenReturn(Optional.of(courseDetailDtoAdmin));
            when(userTrialService.getUsersFromTrial(any()))
                    .thenReturn(List.of(userTrial1));
            when(userService.getUsersFromIdList(anyList()))
                    .thenReturn(List.of(studentUser));

            mvc.perform(get("/public/trials/00000000-0000-0000-0000-000000000001/users")
                            .header("User", studentHeader))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void userIsSuperuser_get200() throws Exception {
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRoleDetail(any(), any()))
                    .thenReturn(Optional.of(courseDetailDtoExternal));
            when(userTrialService.getUsersFromTrial(any()))
                    .thenReturn(List.of(userTrial1));
            when(userService.getUsersFromIdList(anyList()))
                    .thenReturn(List.of(studentUser));

            mvc.perform(get("/public/trials/00000000-0000-0000-0000-000000000001/users")
                            .header("User", superUserHeader))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

    }

    @Nested
    class GetTrialsFromUser {
        @Test
        public void idMismatch_get403() throws Exception {
            mvc.perform(get("/public/trials/users/00000000-0000-0000-0000-000000000042")
                            .header("User", studentHeader))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void userIsSuperuser_get200() throws Exception {
            when(userTrialService.getUsersFromTrial(any()))
                    .thenReturn(List.of(userTrial1));

            mvc.perform(get("/public/trials/users/00000000-0000-0000-0000-000000000042")
                            .header("User", superUserHeader))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void correctId_get200() throws Exception {
            when(userTrialService.getUsersFromTrial(any()))
                    .thenReturn(List.of(userTrial1));

            mvc.perform(get("/public/trials/users/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

}
