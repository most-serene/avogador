package eu.mostserene.avogador.exerciseservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseController;
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseDto;
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService;
import eu.mostserene.avogador.exerciseservice.practices.Practice;
import eu.mostserene.avogador.exerciseservice.trials.ProgrammingLanguage;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExerciseController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ExerciseControllerTests {
    private @Autowired MockMvc mvc;
    private @MockBean BuildProperties buildProperties;
    private @MockBean ProfileManager profileManager;
    private @MockBean ExerciseService exerciseService;
    private @MockBean UserTrialService userTrialService;
    private @MockBean UserCourseService userCourseService;
    private @MockBean TrialService trialService;

    private final Practice practice = new Practice(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Practice One",
            true, true, ProgrammingLanguage.JAVA, Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private final Exercise visibleExercise = new Exercise(practice, "Exercise1", "Given a print b", 1, true);
    private final Exercise hiddenExercise = new Exercise(practice, "Exercise2", "Given b print a", 1, false);
    private final ExerciseDto visibleExerciseDto = new ExerciseDto(UUID.fromString("00000000-0000-0000-0000-000000000001"), UUID.fromString("00000000-0000-0000-0000-000000000001"), "Exercise1", "Given a print b", 1, true);
    private final String studentHeader = "{\"id\":\"00000000-0000-0000-0000-000000000001\", \"email\":\"student@stud.unive.it\", \"givenName\":\"Andy\", \"familyName\":\"Bernard\", \"isProfessor\":false, \"isSuperuser\":false}";
    private final String superUserHeader = "{\"id\":\"00000000-0000-0000-0000-000000000002\", \"email\":\"superuser@stud.unive.it\", \"givenName\":\"Michale\", \"familyName\":\"Scott\", \"isProfessor\":false, \"isSuperuser\":true}";

    private final ObjectMapper mapper = new ObjectMapper();

    @Nested
    class GetExerciseById{
        @Test
        public void wrongExerciseId_get404() throws Exception {
            when(exerciseService.getExercise(any()))
                    .thenReturn(Optional.empty());

            mvc.perform(get("/public/exercises/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void emptyCourseRole_get403() throws Exception {
            when(exerciseService.getExercise(any()))
                    .thenReturn(Optional.of(visibleExercise));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.empty());

            mvc.perform(get("/public/exercises/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void userIsExternal_get403() throws Exception {
            when(exerciseService.getExercise(any()))
                    .thenReturn(Optional.of(visibleExercise));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.EXTERNAL));

            mvc.perform(get("/public/exercises/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void userIsStudentAndExerciseHidden_get403() throws Exception {
            when(exerciseService.getExercise(any()))
                    .thenReturn(Optional.of(hiddenExercise));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.STUDENT));

            mvc.perform(get("/public/exercises/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void userIsStudentAndExerciseVisible_get200() throws Exception {
            when(exerciseService.getExercise(any()))
                    .thenReturn(Optional.of(visibleExercise));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.STUDENT));

            mvc.perform(get("/public/exercises/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void userIsCollaboratorAndExerciseHidden_get200() throws Exception {
            when(exerciseService.getExercise(any()))
                    .thenReturn(Optional.of(hiddenExercise));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.COLLABORATOR));

            mvc.perform(get("/public/exercises/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class CreateExercise {
        @Test
        public void wrongTrialId_get404() throws Exception {
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.empty());

            mvc.perform(post("/public/exercises")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(visibleExerciseDto))
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void emptyCourseRole_get403() throws Exception {
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.empty());

            mvc.perform(post("/public/exercises")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(visibleExerciseDto))
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

            mvc.perform(post("/public/exercises")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(visibleExerciseDto))
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
            when(exerciseService.createExercise(any(), any()))
                    .thenReturn(visibleExercise);

            mvc.perform(post("/public/exercises")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(visibleExerciseDto))
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class UpdateExercise {
        @Test
        public void wrongTrialId_get404() throws Exception {
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.empty());

            mvc.perform(put("/public/exercises/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(visibleExerciseDto))
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void emptyCourseRole_get403() throws Exception {
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.empty());

            mvc.perform(put("/public/exercises/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(visibleExerciseDto))
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromStudent_get403() throws Exception {
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.STUDENT));

            mvc.perform(put("/public/exercises/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(visibleExerciseDto))
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void wrongExerciseId_get404() throws Exception {
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.COLLABORATOR));
            when(exerciseService.getExercise(any()))
                    .thenReturn(Optional.empty());

            mvc.perform(put("/public/exercises/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(visibleExerciseDto)) // id ends with 01
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void fromCollaboratorAndExerciseIdMismatch_get403() throws Exception {
            var exerciseWithId = new Exercise(practice, "Exercise1", "Given a print b", 1, true);
            Field id = exerciseWithId.getClass().getDeclaredField("id");
            id.setAccessible(true);
            id.set(exerciseWithId, UUID.fromString("00000000-0000-0000-0000-000000000002"));

            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.COLLABORATOR));
            when(exerciseService.getExercise(any()))
                    .thenReturn(Optional.of(exerciseWithId));

            mvc.perform(put("/public/exercises/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(visibleExerciseDto)) // id ends with 01
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void fromCollaboratorAndTrialIdMismatch_get403() throws Exception {
            var trialWithId = new Practice(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Practice One",
                    true, true, ProgrammingLanguage.JAVA, Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
            Field tId = trialWithId.getClass().getSuperclass().getDeclaredField("id");
            tId.setAccessible(true);
            tId.set(trialWithId, UUID.fromString("00000000-0000-0000-0000-000000000002"));

            var exerciseWithId = new Exercise(trialWithId, "Exercise1", "Given a print b", 1, true);
            Field eId = exerciseWithId.getClass().getDeclaredField("id");
            eId.setAccessible(true);
            eId.set(exerciseWithId, UUID.fromString("00000000-0000-0000-0000-000000000001"));

            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(trialWithId));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.COLLABORATOR));
            when(exerciseService.getExercise(any()))
                    .thenReturn(Optional.of(exerciseWithId));

            mvc.perform(put("/public/exercises/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(visibleExerciseDto)) // id ends with 01
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void fromCollaborator_get200() throws Exception {
            var trialWithId = new Practice(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Practice One",
                    true, true, ProgrammingLanguage.JAVA, Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
            Field tId = trialWithId.getClass().getSuperclass().getDeclaredField("id");
            tId.setAccessible(true);
            tId.set(trialWithId, UUID.fromString("00000000-0000-0000-0000-000000000001"));

            var exerciseWithId = new Exercise(trialWithId, "Exercise1", "Given a print b", 1, true);
            Field eId = exerciseWithId.getClass().getDeclaredField("id");
            eId.setAccessible(true);
            eId.set(exerciseWithId, UUID.fromString("00000000-0000-0000-0000-000000000001"));

            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(trialWithId));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.COLLABORATOR));
            when(exerciseService.getExercise(any()))
                    .thenReturn(Optional.of(exerciseWithId));
            when(exerciseService.updateExercise(any()))
                    .thenReturn(visibleExercise);

            mvc.perform(put("/public/exercises/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(visibleExerciseDto)) // id ends with 01
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void fromAdmin_get200() throws Exception {
            var trialWithId = new Practice(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Practice One",
                    true, true, ProgrammingLanguage.JAVA, Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
            Field tId = trialWithId.getClass().getSuperclass().getDeclaredField("id");
            tId.setAccessible(true);
            tId.set(trialWithId, UUID.fromString("00000000-0000-0000-0000-000000000001"));

            var exerciseWithId = new Exercise(trialWithId, "Exercise1", "Given a print b", 1, true);
            Field eId = exerciseWithId.getClass().getDeclaredField("id");
            eId.setAccessible(true);
            eId.set(exerciseWithId, UUID.fromString("00000000-0000-0000-0000-000000000001"));

            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(trialWithId));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.ADMIN));
            when(exerciseService.getExercise(any()))
                    .thenReturn(Optional.of(exerciseWithId));
            when(exerciseService.updateExercise(any()))
                    .thenReturn(visibleExercise);

            mvc.perform(put("/public/exercises/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(visibleExerciseDto)) // id ends with 01
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class DeleteExercise {
        @Test
        public void wrongExerciseId_get404() throws Exception{
            when(exerciseService.getExercise(any()))
                    .thenReturn(Optional.empty());

            mvc.perform(delete("/public/exercises/00000000-0000-0000-0000-000000000001")
                        .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void emptyExerciseTrial_get404() throws Exception{
            when(exerciseService.getExercise(any()))
                    .thenReturn(Optional.of(visibleExercise));
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.empty());

            mvc.perform(delete("/public/exercises/00000000-0000-0000-0000-000000000001")
                        .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void emptyCourseRole_get403() throws Exception{
            when(exerciseService.getExercise(any()))
                    .thenReturn(Optional.of(visibleExercise));
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn((Optional.empty()));

            mvc.perform(delete("/public/exercises/00000000-0000-0000-0000-000000000001")
                        .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void userIsExternal_get403() throws Exception{
            when(exerciseService.getExercise(any()))
                    .thenReturn(Optional.of(visibleExercise));
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn((Optional.of(CourseRole.EXTERNAL)));

            mvc.perform(delete("/public/exercises/00000000-0000-0000-0000-000000000001")
                        .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void userIsStudent_get403() throws Exception{
            when(exerciseService.getExercise(any()))
                    .thenReturn(Optional.of(visibleExercise));
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn((Optional.of(CourseRole.STUDENT)));

            mvc.perform(delete("/public/exercises/00000000-0000-0000-0000-000000000001")
                        .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void userIsCollaborator_get200() throws Exception{
            when(exerciseService.getExercise(any()))
                    .thenReturn(Optional.of(visibleExercise));
            when(trialService.getTrialById(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn((Optional.of(CourseRole.COLLABORATOR)));

            mvc.perform(delete("/public/exercises/00000000-0000-0000-0000-000000000001")
                        .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }


}
