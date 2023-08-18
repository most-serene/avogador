package eu.mostserene.avogador.apigateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@Slf4j
public class AuthService {

    public Optional<AuthUserDTO> decodeAndValidateJWT(String jwtToken) {
        AuthUserDTO user = new RestTemplateBuilder()
                .build()
                .postForObject("http://users/security/validate-jwt", jwtToken, AuthUserDTO.class);

        return user != null ? Optional.of(user) : Optional.empty();
    }

    public Optional<AuthUserDTO> decodeAndValidateApiKey(String apiKey) {
        AuthUserDTO user = new RestTemplateBuilder()
                .build()
                .postForObject("http://users/security/validate-key", apiKey, AuthUserDTO.class);

        return user != null ? Optional.of(user) : Optional.empty();
    }
}
