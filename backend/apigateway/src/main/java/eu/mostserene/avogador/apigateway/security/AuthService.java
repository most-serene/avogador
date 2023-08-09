package eu.mostserene.avogador.apigateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Component
@Slf4j
public class AuthService {

    public Optional<AuthUserDTO> decodeAndValidateJWT(String jwtToken) {
        var a = buildAuthRequest("validate-jwt", jwtToken);
        if (a.isPresent()) {
            return sendAuthRequest(a.get());
        }
        return Optional.empty();
    }

    public Optional<AuthUserDTO> decodeAndValidateApiKey(String apiKey) {
        var a = buildAuthRequest("validate-key", apiKey);
        if (a.isPresent()) {
            return sendAuthRequest(a.get());
        }
        return Optional.empty();
    }

    private Optional<HttpRequest> buildAuthRequest(String authEndpoint, String claim) {
        try {
            log.info(new URI("http://users/security/" + authEndpoint).toString());
            return Optional.of(HttpRequest
                    .newBuilder()
                    .uri(new URI("http://users/security/" + authEndpoint))
                    .header("Content-Type", "text/plain")
                    .POST(HttpRequest.BodyPublishers.ofString(claim))
                    .build());
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<AuthUserDTO> sendAuthRequest(HttpRequest httpRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return Optional.of(objectMapper.readValue(
                    HttpClient.newHttpClient()
                            .send(httpRequest, HttpResponse.BodyHandlers.ofString())
                            .body(), AuthUserDTO.class));
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }
}
