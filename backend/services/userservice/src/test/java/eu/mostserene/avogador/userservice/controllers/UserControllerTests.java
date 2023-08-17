package eu.mostserene.avogador.userservice.controllers;

import eu.mostserene.avogador.userservice.mail.EmailService;
import eu.mostserene.avogador.userservice.security.AuthService;
import eu.mostserene.avogador.userservice.users.*;
import eu.mostserene.avogador.userservice.utils.ProfileManager;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTests {
    private @Autowired MockMvc mvc;
    private @MockBean UserRepository repository;
    private @MockBean AuthService authService;
    private @MockBean EmailService emailService;
    private @MockBean UserService userService;
    private @MockBean ProfileManager profileManager;


    private final User student1 = new User("student1@stud.unive.it", "Andy", "Bernard");
    private final User student2 = new User("student2@stud.unive.it", "Angela", "Martin");
    private final User professor = new User("professor@unive.it", "Dwight", "Schrute");
    private final User superuser = new User("superuser@stud.unive.it", "Michael", "Scott");
    private final String student1Header = "{\"id\":\"00000000-0000-0000-0000-000000000001\", \"email\":\"student1@stud.unive.it\", \"givenName\":\"Andy\", \"familyName\":\"Bernard\", \"isProfessor\":false, \"isSuperuser\":false}";
    private final String student2Header = "{\"id\":\"00000000-0000-0000-0000-000000000002\", \"email\":\"student2@stud.unive.it\", \"givenName\":\"Angela\", \"familyName\":\"Martin\", \"isProfessor\":false, \"isSuperuser\":false}";
    private final String professorHeader = "{\"id\":\"00000000-0000-0000-0000-000000000003\", \"email\":\"professor@stud.unive.it\", \"givenName\":\"Dwight\", \"familyName\":\"Schrute\", \"isProfessor\":true, \"isSuperuser\":false}";
    private final String superuserHeader = "{\"id\":\"00000000-0000-0000-0000-000000000004\", \"email\":\"superuser@stud.unive.it\", \"givenName\":\"Michael\", \"familyName\":\"Scott\", \"isProfessor\":false, \"isSuperuser\":true}";
    private final ResponseCookie cookie = ResponseCookie.from("testing-jwt")
            .value(null)
            .httpOnly(true)
            .path("/")
            .secure(true)
            .maxAge(Duration.ofSeconds(1))
            .sameSite("None")
            .build();

    @Nested
    class GetUserById {
        @Test
        public void wrongId_get404() throws Exception {
            when(userService.getUserById(any()))
                    .thenReturn(Optional.empty());

            mvc.perform(get("/public/users/00000000-0000-0000-0000-000000000005").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void fromDifferentStudent_get200() throws Exception {
            when(userService.getUserById(any()))
                    .thenReturn(Optional.of(student2));

            mvc.perform(get("/public/users/00000000-0000-0000-0000-000000000002").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").isEmpty());
        }

        @Test
        public void fromSelf_get200() throws Exception {
            when(userService.getUserById(any()))
                    .thenReturn(Optional.of(student1));

            mvc.perform(get("/public/users/00000000-0000-0000-0000-000000000001").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").isNotEmpty());
        }

        @Test
        public void fromProfessor_get200() throws Exception {
            when(userService.getUserById(any()))
                    .thenReturn(Optional.of(student1));

            mvc.perform(get("/public/users/00000000-0000-0000-0000-000000000001").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").isNotEmpty());
        }

        @Test
        public void fromSuperuser_get200() throws Exception {
            when(userService.getUserById(any()))
                    .thenReturn(Optional.of(student1));

            mvc.perform(get("/public/users/00000000-0000-0000-0000-000000000001").header("User", superuserHeader))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").isNotEmpty());
        }
    }

    @Nested
    class GetUserByEmail {
        @Test
        public void wrongEmail_get404() throws Exception {
            when(userService.getUserByEmail(anyString()))
                    .thenReturn(Optional.empty());

            mvc.perform(get("/public/users/email/jim.halpert@stud.unive.it"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void everythingRight_get200() throws Exception {
            when(userService.getUserByEmail(anyString()))
                    .thenReturn(Optional.of(superuser));

            mvc.perform(get("/public/users/email/superuser@stud.unive.it"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class DeleteUser {
        @Test
        public void wrongId_get404() throws Exception {
            when(userService.getUserById(any()))
                    .thenReturn(Optional.empty());

            mvc.perform(delete("/public/users/00000000-0000-0000-0000-000000000005").header("User", superuserHeader))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void fromStudentDeleteOther_get403() throws Exception {
            when(userService.getUserById(any()))
                    .thenReturn(Optional.of(superuser));

            mvc.perform(delete("/public/users/00000000-0000-0000-0000-000000000004").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromSuperuser_get200() throws Exception {
            when(userService.getUserById(any()))
                    .thenReturn(Optional.of(student1));

            mvc.perform(delete("/public/users/00000000-0000-0000-0000-000000000001").header("User", superuserHeader))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void fromSelf_get200() throws Exception {
            when(userService.getUserById(any()))
                    .thenReturn(Optional.of(student1));

            mvc.perform(delete("/public/users/00000000-0000-0000-0000-000000000001").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    // skip google-auth

    @Test
    public void logoutUser() throws Exception {
        when(profileManager.executeOnProfile(any(), any(), any(), any()))
                .thenReturn(cookie);

        mvc.perform(get("/public/users/logout"))
                .andDo(print())
                .andExpect(cookie().exists("testing-jwt"))
                .andExpect(cookie().maxAge("testing-jwt", 1));
    }

    @Nested
    class RevokeJWT {
        @Test
        public void fromStudent_get403() throws Exception {
            mvc.perform(patch("/public/users/00000000-0000-0000-0000-000000000002/revoke-jwt").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromProfessor_get403() throws Exception {
            mvc.perform(patch("/public/users/00000000-0000-0000-0000-000000000001/revoke-jwt").header("User", professorHeader))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromSuperuser_get200() throws Exception {
            mvc.perform(patch("/public/users/00000000-0000-0000-0000-000000000001/revoke-jwt").header("User", superuserHeader))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void fromSelf_get200() throws Exception {
            mvc.perform(patch("/public/users/00000000-0000-0000-0000-000000000001/revoke-jwt").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }
}
