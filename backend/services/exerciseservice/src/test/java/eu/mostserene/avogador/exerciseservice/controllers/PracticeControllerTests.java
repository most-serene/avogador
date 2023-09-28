package eu.mostserene.avogador.exerciseservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.filesystem.FileSystemService;
import eu.mostserene.avogador.exerciseservice.practices.Practice;
import eu.mostserene.avogador.exerciseservice.practices.PracticeController;
import eu.mostserene.avogador.exerciseservice.practices.PracticeRepository;
import eu.mostserene.avogador.exerciseservice.practices.PracticeService;
import eu.mostserene.avogador.exerciseservice.trials.ProgrammingLanguage;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PracticeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PracticeControllerTests {
    private @Autowired MockMvc mvc;
    private @MockBean PracticeService practiceService;
    private @MockBean PracticeRepository practiceRepository;
    private @MockBean UserCourseService userCourseService;
    private @MockBean BuildProperties buildProperties;
    private @MockBean ProfileManager profileManager;
    private @MockBean FileSystemService fileSystemService;


    private final Practice practice = new Practice(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Practice One",
            true, true, ProgrammingLanguage.JAVA, Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));

    private final Practice notVisibilePractice = new Practice(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Practice One",
            false, true, ProgrammingLanguage.JAVA, Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));

    private final Practice privateNotVisisblePractice = new Practice(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Practice One",
            false, false, ProgrammingLanguage.JAVA, Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));

    private final String studentHeader = "{\"id\":\"00000000-0000-0000-0000-000000000001\", \"email\":\"student@stud.unive.it\", \"givenName\":\"Andy\", \"familyName\":\"Bernard\", \"isProfessor\":false, \"isSuperuser\":false}";
    private final String superUserHeader = "{\"id\":\"00000000-0000-0000-0000-000000000001\", \"email\":\"superuser@stud.unive.it\", \"givenName\":\"Andy\", \"familyName\":\"Bernard\", \"isProfessor\":false, \"isSuperuser\":true}";

    @Nested
    class GetPractice {
        @Test
        public void notExisting_get404() throws Exception {
            when(practiceService.getPractice(any()))
                    .thenReturn(Optional.empty());

            mvc.perform(get("/public/trials/practices/00000000-0000-0000-0000-000000000000")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }


        @Test
        public void fromSuperuser_get200() throws Exception {
            when(practiceService.getPractice(any()))
                    .thenReturn(Optional.of(practice));
            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.EXTERNAL));

            mvc.perform(get("/public/trials/practices/00000000-0000-0000-0000-000000000000")
                            .header("User", superUserHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void fromExternalGet_get403() throws Exception {
            when(practiceService.getPractice(any()))
                    .thenReturn(Optional.of(practice));

            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.EXTERNAL));

            mvc.perform(get("/public/trials/practices/00000000-0000-0000-0000-000000000000")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromStudentWithNotVisible_get403() throws Exception {
            when(practiceService.getPractice(any()))
                    .thenReturn(Optional.of(notVisibilePractice));

            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.STUDENT));

            mvc.perform(get("/public/trials/practices/00000000-0000-0000-0000-000000000000")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromCollaborator_get200() throws Exception {
            when(practiceService.getPractice(any()))
                    .thenReturn(Optional.of(privateNotVisisblePractice));

            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.COLLABORATOR));

            mvc.perform(get("/public/trials/practices/00000000-0000-0000-0000-000000000000")
                            .header("User", studentHeader)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }

    }


    @Nested
    class CreatePractice {
        @Test
        public void fromExternal_get403() throws Exception {
            ObjectMapper mapper = new ObjectMapper();

            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.EXTERNAL));

            mvc.perform(post("/public/trials/practices")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(practice))
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromStudent_get403() throws Exception {
            ObjectMapper mapper = new ObjectMapper();

            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.STUDENT));

            mvc.perform(post("/public/trials/practices")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(practice))
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromCollaborator_get200() throws Exception {
            ObjectMapper mapper = new ObjectMapper();

            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.COLLABORATOR));

            mvc.perform(post("/public/trials/practices")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(practice))
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void fromSuperuser_get200() throws Exception {
            ObjectMapper mapper = new ObjectMapper();

            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.EXTERNAL));

            mvc.perform(post("/public/trials/practices")
                            .header("User", superUserHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(practice))
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class UpdatePractice {
        @Test
        public void practiceNotExisting_get404() throws Exception {
            ObjectMapper mapper = new ObjectMapper();
            Field id = practice.getClass().getSuperclass().getDeclaredField("id");
            id.setAccessible(true);
            id.set(practice, UUID.fromString("00000000-0000-0000-0000-000000000001"));

            when(practiceService.getPractice(any()))
                    .thenReturn(Optional.empty());

            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.EXTERNAL));

            mvc.perform(put("/public/trials/practices/00000000-0000-0000-0000-000000000000")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(practice))
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void fromStudent_get403() throws Exception {
            ObjectMapper mapper = new ObjectMapper();
            Field id = practice.getClass().getSuperclass().getDeclaredField("id");
            id.setAccessible(true);
            id.set(practice, UUID.fromString("00000000-0000-0000-0000-000000000001"));

            when(practiceService.getPractice(any()))
                    .thenReturn(Optional.of(practice));

            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.STUDENT));

            mvc.perform(put("/public/trials/practices/00000000-0000-0000-0000-000000000000")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(practice))
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromCollaborator_get200() throws Exception {
            ObjectMapper mapper = new ObjectMapper();

            Field id = practice.getClass().getSuperclass().getDeclaredField("id");
            id.setAccessible(true);
            id.set(practice, UUID.fromString("00000000-0000-0000-0000-000000000001"));

            Field privatePracticeId = notVisibilePractice.getClass().getSuperclass().getDeclaredField("id");
            privatePracticeId.setAccessible(true);
            privatePracticeId.set(notVisibilePractice, UUID.fromString("00000000-0000-0000-0000-000000000001"));

            when(practiceService.getPractice(any()))
                    .thenReturn(Optional.of(practice));

            when(practiceService.updatePractice(any()))
                    .thenReturn(notVisibilePractice);

            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.COLLABORATOR));

            mvc.perform(put("/public/trials/practices/00000000-0000-0000-0000-000000000001")
                            .header("User", studentHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(notVisibilePractice))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isVisible").value(false));
        }

        @Test
        public void fromSuperuser_get200() throws Exception {
            ObjectMapper mapper = new ObjectMapper();

            Field id = practice.getClass().getSuperclass().getDeclaredField("id");
            id.setAccessible(true);
            id.set(practice, UUID.fromString("00000000-0000-0000-0000-000000000001"));

            Field privatePracticeId = notVisibilePractice.getClass().getSuperclass().getDeclaredField("id");
            privatePracticeId.setAccessible(true);
            privatePracticeId.set(notVisibilePractice, UUID.fromString("00000000-0000-0000-0000-000000000001"));

            when(practiceService.getPractice(any()))
                    .thenReturn(Optional.of(practice));

            when(practiceService.updatePractice(any()))
                    .thenReturn(notVisibilePractice);

            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.EXTERNAL));

            mvc.perform(put("/public/trials/practices/00000000-0000-0000-0000-000000000001")
                            .header("User", superUserHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(notVisibilePractice))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isVisible").value(false));
        }

        @Test
        public void idMismatch_get400() throws Exception {
            ObjectMapper mapper = new ObjectMapper();

            Field id = practice.getClass().getSuperclass().getDeclaredField("id");
            id.setAccessible(true);
            id.set(practice, UUID.fromString("00000000-0000-0000-0000-000000000001"));

            Field privatePracticeId = notVisibilePractice.getClass().getSuperclass().getDeclaredField("id");
            privatePracticeId.setAccessible(true);
            privatePracticeId.set(notVisibilePractice, UUID.fromString("00000000-0000-0000-0000-000000000002"));

            when(practiceService.getPractice(any()))
                    .thenReturn(Optional.of(practice));

            when(practiceService.updatePractice(any()))
                    .thenReturn(notVisibilePractice);

            when(userCourseService.getUserCourseRole(any(), any()))
                    .thenReturn(Optional.of(CourseRole.EXTERNAL));

            mvc.perform(put("/public/trials/practices/00000000-0000-0000-0000-000000000001")
                            .header("User", superUserHeader)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(notVisibilePractice))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

    }
}
