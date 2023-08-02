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

@Component
@Slf4j
public class AuthService {

    public AuthUserDTO decodeAndValidateJWT(String jwtToken) {
        ObjectMapper objectMapper = new ObjectMapper();

        HttpRequest httpRequest = null;
        try {

            httpRequest = HttpRequest
                    .newBuilder()
                    .uri(new URI("http://users/security/validate-jwt"))
                    .header("Content-Type", "text/plain")
                    .POST(HttpRequest.BodyPublishers.ofString(jwtToken))
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpResponse<String> content = null;
        try {
            content = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            log.info(content.body());
            return objectMapper.readValue(content.body(), AuthUserDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
