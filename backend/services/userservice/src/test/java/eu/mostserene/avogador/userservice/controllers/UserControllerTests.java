package eu.mostserene.avogador.userservice.controllers;

import eu.mostserene.avogador.userservice.apikey.ApiKey;
import eu.mostserene.avogador.userservice.apikey.ApiKeyService;
import eu.mostserene.avogador.userservice.mail.EmailService;
import eu.mostserene.avogador.userservice.security.AuthServiceImpl;
import eu.mostserene.avogador.userservice.users.*;
import eu.mostserene.avogador.userservice.utils.ProfileManager;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
    private @MockBean AuthServiceImpl authService;
    private @MockBean EmailService emailService;
    private @MockBean UserService userService;
    private @MockBean ApiKeyService apiKeyService;
    private @MockBean ProfileManager profileManager;


    private final AuthUserDTO student1Dto = new AuthUserDTO(1L, "student1@stud.unive.it", "Andy", "Bernard", false, false);
    private final AuthUserDTO student2Dto = new AuthUserDTO(2L, "student2@stud.unive.it", "Angela", "Martin", false, false);
    private final AuthUserDTO professorDto = new AuthUserDTO(3L, "professor@unive.it", "Dwight", "Schrute", true, false);
    private final AuthUserDTO superuserDto = new AuthUserDTO(4L, "superuser@stud.unive.it", "Michael", "Scott", false, true);
    private final User student1 = new User("student1@stud.unive.it", "Andy", "Bernard");
    private final User student2 = new User("student2@stud.unive.it", "Angela", "Martin");
    private final User professor = new User("professor@unive.it", "Dwight", "Schrute");
    private final User superuser = new User("superuser@stud.unive.it", "Michael", "Scott");
    private final ResponseCookie cookie = ResponseCookie.from("testing-jwt")
            .value(null)
            .httpOnly(true)
            .path("/")
            .secure(false)
            .maxAge(Duration.ofSeconds(1))
            .sameSite("None")
            .build();

    @Nested
    class GetUserById {
        @Test
        public void wrongId_get404() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(student1Dto);
            when(userService.getUserById(anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(get("/public/users/5"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void fromDifferentStudent_get200() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(student1Dto);
            when(userService.getUserById(anyLong()))
                    .thenReturn(Optional.of(student2));

            mvc.perform(get("/public/users/2"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").isEmpty());
        }

        @Test
        public void fromSelf_get200() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(student1Dto);
            when(userService.getUserById(anyLong()))
                    .thenReturn(Optional.of(student1));

            mvc.perform(get("/public/users/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").isNotEmpty());
        }

        @Test
        public void fromProfessor_get200() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(professorDto);
            when(userService.getUserById(anyLong()))
                    .thenReturn(Optional.of(student1));

            mvc.perform(get("/public/users/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").isNotEmpty());
        }

        @Test
        public void fromSuperuser_get200() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(superuserDto);
            when(userService.getUserById(anyLong()))
                    .thenReturn(Optional.of(student1));

            mvc.perform(get("/public/users/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").isNotEmpty());
        }
    }

    @Nested
    class GetUserByEmail {
        @Test
        public void wrongEmail_get404() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(professorDto);
            when(userService.getUserByEmail(anyString()))
                    .thenReturn(Optional.empty());

            mvc.perform(get("/public/users/email/jim.halpert@stud.unive.it"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void everythingRight_get200() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(professorDto);
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
            when(authService.getRequestUser(any()))
                    .thenReturn(superuserDto);
            when(userService.getUserById(anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(delete("/public/users/5"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void fromStudentDeleteOther_get403() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(student1Dto);
            when(userService.getUserById(anyLong()))
                    .thenReturn(Optional.of(superuser));

            mvc.perform(delete("/public/users/4"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromSuperuser_get200() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(superuserDto);
            when(userService.getUserById(anyLong()))
                    .thenReturn(Optional.of(student1));

            mvc.perform(delete("/public/users/1"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void fromSelf_get200() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(student1Dto);
            when(userService.getUserById(anyLong()))
                    .thenReturn(Optional.of(student1));

            mvc.perform(delete("/public/users/1"))
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
            when(authService.getRequestUser(any()))
                    .thenReturn(student2Dto);

            mvc.perform(patch("/public/users/1/revoke-jwt"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromProfessor_get403() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(professorDto);

            mvc.perform(patch("/public/users/1/revoke-jwt"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void fromSuperuser_get200() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(superuserDto);

            mvc.perform(patch("/public/users/1/revoke-jwt"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void fromSelf_get200() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(student1Dto);

            mvc.perform(patch("/public/users/1/revoke-jwt"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class GetApiKeys {
        @Test
        public void idMismatch_get403() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(student2Dto);

            mvc.perform(get("/public/users/1/api-key"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void wrongId_get404() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(student1Dto);
            when(userService.getUserById(anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(get("/public/users/1/api-key"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void everythingRight_get200() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(student1Dto);
            when(userService.getUserById(anyLong()))
                    .thenReturn(Optional.of(student1));
            when(apiKeyService.getApiKeyByUser(any()))
                    .thenReturn(List.of());

            mvc.perform(get("/public/users/1/api-key"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class GenerateApiKey {
        @Test
        public void mismatchId_get403() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(student2Dto);

            mvc.perform(post("/public/users/1/api-key")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\": \"key\", \"expiration\": \"2016-02-16 11:00:02\"}"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void wrongId_get404() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(student1Dto);
            when(userService.getUserById(anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(post("/public/users/1/api-key")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\": \"key\", \"expiration\": \"2016-02-16 11:00:02\"}"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void nameWithSpaces_get400() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(student1Dto);
            when(userService.getUserById(anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(post("/public/users/1/api-key")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\": \"key with spaces\", \"expiration\": \"2016-02-16 11:00:02\"}"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void everythingRight_get200() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(student1Dto);
            when(userService.getUserById(anyLong()))
                    .thenReturn(Optional.of(student1));

            mvc.perform(post("/public/users/1/api-key")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\": \"key\", \"expiration\": \"2024-04-20T11:00:00Z\"}"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class DeleteApiKey {
        @Test
        public void mismatchId_get403() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(student2Dto);

            mvc.perform(delete("/public/users/1/api-key/key"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void wrongUserId_get404() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(student1Dto);
            when(userService.getUserById(anyLong()))
                    .thenReturn(Optional.empty());

            mvc.perform(delete("/public/users/1/api-key/key"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void wrongKeyName_get404() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(student1Dto);
            when(userService.getUserById(anyLong()))
                    .thenReturn(Optional.of(student1));
            when(apiKeyService.getApiKeyByName(any(), anyString()))
                    .thenReturn(Optional.empty());

            mvc.perform(delete("/public/users/1/api-key/key"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void everythingRight_get200() throws Exception {
            when(authService.getRequestUser(any()))
                    .thenReturn(student1Dto);
            when(userService.getUserById(anyLong()))
                    .thenReturn(Optional.of(student1));
            when(apiKeyService.getApiKeyByName(any(), anyString()))
                    .thenReturn(Optional.of(new ApiKey()));

            mvc.perform(delete("/public/users/1/api-key/key"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

}
