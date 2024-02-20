package eu.mostserene.avogador.userservice.profilemanager;

import eu.mostserene.avogador.userservice.users.User;
import lombok.Getter;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Getter
@Service
public class TestingProfile implements Profile{
    private final String JWTKey = "testing-jwt";

    public ResponseCookie buildJWT(User user, String value) {
        return ResponseCookie.from(JWTKey)
                .value(value)
                .httpOnly(true)
                .path("/")
                .secure(true)
                .maxAge(Duration.ofDays(7))
                .sameSite("None")
                .build();
    }

    public ResponseCookie logout() {
        return ResponseCookie.from(JWTKey)
                .value(null)
                .httpOnly(true)
                .path("/")
                .secure(true)
                .maxAge(Duration.ofSeconds(1))
                .sameSite("None")
                .build();
    }
}
