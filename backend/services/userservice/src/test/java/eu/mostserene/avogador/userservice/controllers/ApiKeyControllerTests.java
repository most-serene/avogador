package eu.mostserene.avogador.userservice.controllers;

import eu.mostserene.avogador.userservice.apikey.ApiKey;
import eu.mostserene.avogador.userservice.apikey.ApiKeyController;
import eu.mostserene.avogador.userservice.apikey.ApiKeyService;
import eu.mostserene.avogador.userservice.security.AuthService;
import eu.mostserene.avogador.userservice.users.*;
import eu.mostserene.avogador.userservice.utils.ProfileManager;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiKeyController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApiKeyControllerTests {
    private @Autowired MockMvc mvc;
    private @MockBean AuthService authService;
    private @MockBean UserService userService;
    private @MockBean ApiKeyService apiKeyService;
    private @MockBean ProfileManager profileManager;

    private final User student1 = new User("student1@stud.unive.it", "Andy", "Bernard");
    private final String student1Header = "{\"id\":\"00000000-0000-0000-0000-000000000001\", \"email\":\"student1@stud.unive.it\", \"givenName\":\"Andy\", \"familyName\":\"Bernard\", \"isProfessor\":false, \"isSuperuser\":false}";

    @Nested
    class GetApiKeys {
        @Test
        public void idMismatch_get403() throws Exception {
            mvc.perform(get("/public/users/00000000-0000-0000-0000-000000000002/api-key").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void wrongId_get404() throws Exception {
            when(userService.getUserById(any()))
                    .thenReturn(Optional.empty());

            mvc.perform(get("/public/users/00000000-0000-0000-0000-000000000001/api-key").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void everythingRight_get200() throws Exception {
            when(userService.getUserById(any()))
                    .thenReturn(Optional.of(student1));
            when(apiKeyService.getApiKeyByUser(any()))
                    .thenReturn(List.of());

            mvc.perform(get("/public/users/00000000-0000-0000-0000-000000000001/api-key").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class GenerateApiKey {
        @Test
        public void mismatchId_get403() throws Exception {
            mvc.perform(post("/public/users/00000000-0000-0000-0000-000000000002/api-key")
                            .header("User", student1Header)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\": \"key\", \"expiration\": \"2016-02-16 11:00:02\"}"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void wrongId_get404() throws Exception {
            when(userService.getUserById(any()))
                    .thenReturn(Optional.empty());

            mvc.perform(post("/public/users/00000000-0000-0000-0000-000000000001/api-key")
                            .header("User", student1Header)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\": \"key\", \"expiration\": \"2016-02-16 11:00:02\"}"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void nameWithSpaces_get400() throws Exception {
            when(userService.getUserById(any()))
                    .thenReturn(Optional.empty());

            mvc.perform(post("/public/users/00000000-0000-0000-0000-000000000001/api-key")
                            .header("User", student1Header)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\": \"key with spaces\", \"expiration\": \"2016-02-16 11:00:02\"}"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void everythingRight_get200() throws Exception {
            when(userService.getUserById(any()))
                    .thenReturn(Optional.of(student1));

            mvc.perform(post("/public/users/00000000-0000-0000-0000-000000000001/api-key")
                            .header("User", student1Header)
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
            mvc.perform(delete("/public/users/00000000-0000-0000-0000-000000000002/api-key/key").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        public void wrongUserId_get404() throws Exception {
            when(userService.getUserById(any()))
                    .thenReturn(Optional.empty());

            mvc.perform(delete("/public/users/00000000-0000-0000-0000-000000000001/api-key/key").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void wrongKeyName_get404() throws Exception {
            when(userService.getUserById(any()))
                    .thenReturn(Optional.of(student1));
            when(apiKeyService.getApiKeyByName(any(), anyString()))
                    .thenReturn(Optional.empty());

            mvc.perform(delete("/public/users/00000000-0000-0000-0000-000000000001/api-key/key").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void everythingRight_get200() throws Exception {
            when(userService.getUserById(any()))
                    .thenReturn(Optional.of(student1));
            when(apiKeyService.getApiKeyByName(any(), anyString()))
                    .thenReturn(Optional.of(new ApiKey()));

            mvc.perform(delete("/public/users/00000000-0000-0000-0000-000000000001/api-key/key").header("User", student1Header))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

}
