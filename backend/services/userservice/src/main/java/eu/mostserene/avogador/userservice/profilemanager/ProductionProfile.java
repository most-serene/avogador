package eu.mostserene.avogador.userservice.profilemanager;

import eu.mostserene.avogador.userservice.users.User;
import lombok.Getter;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Getter
@Service
public class ProductionProfile implements Profile{
    private final String JWTKey = "__Secure-jwt" ;
    public ResponseCookie buildJWT(User user, String value) {
        return ResponseCookie.from(JWTKey)
                .value(value)
                .httpOnly(true)
                .path("/")
                // .domain("api.avogador.mostserene.eu")
                .secure(true)
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax")
                .build();
    }
    public ResponseCookie logout() {
        return ResponseCookie.from(JWTKey)
                .value(null)
                .httpOnly(true)
                .path("/")
                // .domain("api.avogador.mostserene.eu")
                .secure(true)
                .maxAge(Duration.ofSeconds(1))
                .sameSite("Lax")
                .build();
    }
}
