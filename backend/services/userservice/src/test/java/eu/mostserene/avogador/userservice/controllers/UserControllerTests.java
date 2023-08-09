package eu.mostserene.avogador.userservice.controllers;

import eu.mostserene.avogador.userservice.apikey.ApiKeyService;
import eu.mostserene.avogador.userservice.mail.EmailService;
import eu.mostserene.avogador.userservice.security.AuthServiceImpl;
import eu.mostserene.avogador.userservice.users.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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
    private @MockBean AuthServiceImpl authService;
    private @MockBean EmailService emailService;
    private @MockBean UserService userService;
    private @MockBean ApiKeyService apiKeyService;


    private final AuthUserDTO student1Dto = new AuthUserDTO(1L, "student1@stud.unive.it", "Andy", "Bernard", false, false);
    private final AuthUserDTO student2Dto = new AuthUserDTO(2L, "student2@stud.unive.it", "Angela", "Martin", false, false);
    private final AuthUserDTO professorDto = new AuthUserDTO(3L, "professor@unive.it", "Dwight", "Schrute", true, false);
    private final AuthUserDTO superuserDto = new AuthUserDTO(4L, "superuser@stud.unive.it", "Michael", "Scott", false, true);
    private final User student1 = new User("student1@stud.unive.it", "Andy", "Bernard");
    private final User student2 = new User("student2@stud.unive.it", "Angela", "Martin");
    private final User professor = new User("professor@unive.it", "Dwight", "Schrute");
    private final User superuser = new User("superuser@stud.unive.it", "Michael", "Scott");

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
        mvc.perform(get("/public/users/logout"))
                .andDo(print())
                .andExpect(cookie().exists("__Secure-jwt"));
    }


}
