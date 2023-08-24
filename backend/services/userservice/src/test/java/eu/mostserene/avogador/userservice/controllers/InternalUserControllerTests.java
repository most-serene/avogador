package eu.mostserene.avogador.userservice.controllers;

import eu.mostserene.avogador.userservice.security.AuthService;
import eu.mostserene.avogador.userservice.users.InternalUserController;
import eu.mostserene.avogador.userservice.users.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InternalUserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class InternalUserControllerTests {
    private @Autowired MockMvc mvc;
    private @MockBean AuthService authService;
    private @MockBean UserService userService;

    @Nested
    class GetUsersFromIdList {
        @Test
        public void negativeOffset_get400() throws Exception {
            mvc.perform(patch("/users?offset=-1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("[\"d899c3ae-de98-4bab-8718-7d811d52b4fe\", \"dc7fa2b3-c40f-470a-a104-a8068133e26b\", \"594ba933-aa63-4445-8713-d15df64c0c6f\", \"69f079f9-fc50-4888-b374-ebb311e9ea55\", \"1e4e883d-6fd9-4691-afc7-efeafd18c98f\", \"46d4e8d9-c6eb-4cef-8257-74522e173b91\", \"a609af56-1969-4351-9264-7754dd30bbc5\"]"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void zeroLimit_get400() throws Exception {
            mvc.perform(patch("/users?offset=0&limit=-1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("[\"d899c3ae-de98-4bab-8718-7d811d52b4fe\", \"dc7fa2b3-c40f-470a-a104-a8068133e26b\", \"594ba933-aa63-4445-8713-d15df64c0c6f\", \"69f079f9-fc50-4888-b374-ebb311e9ea55\", \"1e4e883d-6fd9-4691-afc7-efeafd18c98f\", \"46d4e8d9-c6eb-4cef-8257-74522e173b91\", \"a609af56-1969-4351-9264-7754dd30bbc5\"]"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void wrongDirection_get400() throws Exception {
            mvc.perform(patch("/users?offset=0&limit=25&orderBy=givenName&direction=BOH")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("[\"d899c3ae-de98-4bab-8718-7d811d52b4fe\", \"dc7fa2b3-c40f-470a-a104-a8068133e26b\", \"594ba933-aa63-4445-8713-d15df64c0c6f\", \"69f079f9-fc50-4888-b374-ebb311e9ea55\", \"1e4e883d-6fd9-4691-afc7-efeafd18c98f\", \"46d4e8d9-c6eb-4cef-8257-74522e173b91\", \"a609af56-1969-4351-9264-7754dd30bbc5\"]"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void noOrderBy_get200() throws Exception {
            mvc.perform(patch("/users?offset=0&limit=25")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("[\"d899c3ae-de98-4bab-8718-7d811d52b4fe\", \"dc7fa2b3-c40f-470a-a104-a8068133e26b\", \"594ba933-aa63-4445-8713-d15df64c0c6f\", \"69f079f9-fc50-4888-b374-ebb311e9ea55\", \"1e4e883d-6fd9-4691-afc7-efeafd18c98f\", \"46d4e8d9-c6eb-4cef-8257-74522e173b91\", \"a609af56-1969-4351-9264-7754dd30bbc5\"]"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void noDirection_get200() throws Exception {
            mvc.perform(patch("/users?offset=0&limit=25&orderBy=givenName")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("[\"d899c3ae-de98-4bab-8718-7d811d52b4fe\", \"dc7fa2b3-c40f-470a-a104-a8068133e26b\", \"594ba933-aa63-4445-8713-d15df64c0c6f\", \"69f079f9-fc50-4888-b374-ebb311e9ea55\", \"1e4e883d-6fd9-4691-afc7-efeafd18c98f\", \"46d4e8d9-c6eb-4cef-8257-74522e173b91\", \"a609af56-1969-4351-9264-7754dd30bbc5\"]"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

}
