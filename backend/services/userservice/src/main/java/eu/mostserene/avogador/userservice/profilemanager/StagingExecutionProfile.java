package eu.mostserene.avogador.userservice.profilemanager;

import eu.mostserene.avogador.userservice.users.User;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Getter
@Service
public class StagingExecutionProfile implements ExecutionProfile {
    private final String JWTKey = "staging-jwt";

    public ResponseCookie buildJWT(User user, String value) {
        return ResponseCookie.from(JWTKey)
                .value(value)
                .httpOnly(true)
                .path("/")
                .domain("api.avogador.staging.mostserene.eu")
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
                .domain("api.avogador.staging.mostserene.eu")
                .secure(true)
                .maxAge(Duration.ofSeconds(1))
                .sameSite("None")
                .build();
    }
}


@Configuration
@Profile("staging")
class StagingExecutionProfileConfiguration {
    @Bean
    public ExecutionProfile executionProfile(){
        return new StagingExecutionProfile();
    }
}