package eu.mostserene.avogador.userservice.services;

import eu.mostserene.avogador.userservice.security.AuthService;
import eu.mostserene.avogador.userservice.security.ForbiddenException;
import eu.mostserene.avogador.userservice.users.AuthUserDTO;
import eu.mostserene.avogador.userservice.users.User;
import eu.mostserene.avogador.userservice.users.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthServiceIntegrationTests {

    private @Autowired AuthService authService;

    private @MockBean UserService userService;

    @Test
    void generateAndDecodeJWT_correct() throws Exception {
        var userWithId = new User("jim.halpert@unive.it", "Jim", "Halpert");
        Field id = userWithId.getClass().getDeclaredField("id");
        id.setAccessible(true);
        UUID generatedId = UUID.randomUUID();
        System.out.println("\t-- Generated UUID: " + generatedId);
        id.set(userWithId, generatedId);
        userWithId.setJwtValidity(Timestamp.from(Instant.ofEpochMilli(1691784501555L)));

        when(userService.getUserById(any()))
                .thenReturn(Optional.of(userWithId));

        var generatedJwt = assertDoesNotThrow(() -> authService.generateJWT(userWithId, 1000L));
        System.out.println("\t-- Generated JWT: " + generatedJwt);

        var decodedJwt = authService.decodeJwt(generatedJwt);
        assertEquals(generatedId, decodedJwt.getId());
        assertEquals("jim.halpert@unive.it", decodedJwt.getEmail());
        assertEquals("Jim", decodedJwt.getGivenName());
        assertEquals("Halpert", decodedJwt.getFamilyName());
    }

    @Test
    void generateAndDecodeJWT_notValid() throws Exception {
        var userWithId = new User("jim.halpert@unive.it", "Jim", "Halpert");
        Field id = userWithId.getClass().getDeclaredField("id");
        id.setAccessible(true);
        UUID generatedId = UUID.randomUUID();
        System.out.println("\t-- Generated UUID: " + generatedId);
        id.set(userWithId, generatedId);
        userWithId.setJwtValidity(Timestamp.from(Instant.ofEpochMilli(27217845015550L)));

        when(userService.getUserById(any()))
                .thenReturn(Optional.of(userWithId));

        var generatedJwt = assertDoesNotThrow(() -> authService.generateJWT(userWithId, 1000L));
        System.out.println("\t-- Generated JWT: " + generatedJwt);

        assertThrows(ForbiddenException.class, () -> authService.decodeJwt(generatedJwt));
    }


}