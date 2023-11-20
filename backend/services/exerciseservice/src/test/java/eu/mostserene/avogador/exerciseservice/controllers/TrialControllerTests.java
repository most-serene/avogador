package eu.mostserene.avogador.exerciseservice.controllers;

import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseDto;
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService;
import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import eu.mostserene.avogador.exerciseservice.practices.Practice;
import eu.mostserene.avogador.exerciseservice.trials.ProgrammingLanguage;
import eu.mostserene.avogador.exerciseservice.trials.TrialController;
import eu.mostserene.avogador.exerciseservice.trials.TrialService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrialController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TrialControllerTests {
    private @Autowired MockMvc mvc;
    private @MockBean BuildProperties buildProperties;
    private @MockBean ProfileManager profileManager;
    private @MockBean ExerciseService exerciseService;
    private @MockBean UserTrialService userTrialService;
    private @MockBean UserCourseService userCourseService;
    private @MockBean TrialService trialService;
    private @MockBean StorageService storageService;

    private final Practice practice = new Practice(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Practice One",
            true, true, ProgrammingLanguage.JAVA, Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private final Practice hiddenPractice = new Practice(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Practice Hidden",
            false, true, ProgrammingLanguage.JAVA, Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private final Exercise visibleExercise = new Exercise(practice, "Exercise1", "Given a print b", 1, true);
    private final Exercise hiddenExercise = new Exercise(practice, "Exercise2", "Given b print a", 1, false);
    private final ExerciseDto visibleExerciseDto = new ExerciseDto(UUID.fromString("00000000-0000-0000-0000-000000000001"), UUID.fromString("00000000-0000-0000-0000-000000000001"), "Exercise1", "Given a print b", 1, true);
    private final String studentHeader = "{\"id\":\"00000000-0000-0000-0000-000000000001\", \"email\":\"student@stud.unive.it\", \"givenName\":\"Andy\", \"familyName\":\"Bernard\", \"isProfessor\":false, \"isSuperuser\":false}";
    private final String superUserHeader = "{\"id\":\"00000000-0000-0000-0000-000000000002\", \"email\":\"superuser@stud.unive.it\", \"givenName\":\"Michale\", \"familyName\":\"Scott\", \"isProfessor\":false, \"isSuperuser\":true}";


    @Nested
    class GetTrialsFromCourse{
        @Test
        public void emptyRole_get403() throws Exception {
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.empty());

            mvc.perform(get("/public/trials/courses/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void userIsExternal_get403() throws Exception {
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.EXTERNAL));

            mvc.perform(get("/public/trials/courses/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void userIsStudent_get200() throws Exception {
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.STUDENT));
            when(trialService.getTrialsByCourseId(any(), eq(true) ))
                    .thenReturn(List.of(practice, hiddenPractice));
            when(trialService.getTrialsByCourseId(any(), eq(false) ))
                    .thenReturn(List.of(practice));

            mvc.perform(get("/public/trials/courses/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));;
        }

        @Test
        public void userIsCollaborator_get200() throws Exception {
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.COLLABORATOR));
            when(trialService.getTrialsByCourseId(any(), eq(true) ))
                    .thenReturn(List.of(practice, hiddenPractice));
            when(trialService.getTrialsByCourseId(any(), eq(false) ))
                    .thenReturn(List.of(practice));

            mvc.perform(get("/public/trials/courses/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));;
        }
    }

    @Nested
    class DeleteTrial {

        @Test
        public void wrongId_get404() throws Exception {
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.empty());

            mvc.perform(delete("/public/trials/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void emptyRole_get403() throws Exception {
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.empty());

            mvc.perform(delete("/public/trials/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void userIsStudent_get403() throws Exception {
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.STUDENT));

            mvc.perform(delete("/public/trials/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void userIsCollaborator_get200() throws Exception {
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.COLLABORATOR));

            mvc.perform(delete("/public/trials/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

}
