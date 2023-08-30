package eu.mostserene.avogador.apigateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import eu.mostserene.avogador.apigateway.utils.ProfileManager;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

@Component
@Slf4j
public class CustomWebFilter implements WebFilter {

    @Autowired
    private AuthService authService;

    @Autowired
    private ProfileManager profileManager;

    @Override
    @NonNull
    @Order(5)
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String uri = exchange.getRequest().getURI().getPath();
        String requestId = RandomStringUtils.randomAlphanumeric(10);
        String cookieName = getCookieName();

        log.info("Request ID - " + requestId);

        log.info("CSRF - " + isCSRF(exchange.getRequest()));

        if (isCSRF(exchange.getRequest())) {
            log.info(" -- CSRF detected?!");
            ResponseCookie cookie = profileManager.executeOnProfile(
                    this::developLogout,
                    this::testingLogout,
                    this::stagingLogout,
                    this::productionLogout
            );
            // FIXME
            // exchange.getResponse().addCookie(cookie);
            // exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            // return exchange.getResponse().setComplete();
        }

        log.info(uri);
        log.info(String.valueOf(uri.matches("^/api.*")));
        log.info(String.valueOf(!uri.matches("^/api/.*/api-key$")));

        if (uri.matches("^/api.*") && !uri.matches("^/api/.*/api-key$")) {
            if (exchange.getRequest().getCookies().getFirst(cookieName) != null) {
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                return exchange.getResponse().setComplete();
            }
            log.info("This is an API call");

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !"Bearer".equals(authHeader.split("\\s+")[0]) ||
                    authHeader.split("\\s+")[1] == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String apiKey = authHeader.split("\\s+")[1];

            Optional<AuthUserDTO> user = authService.decodeAndValidateApiKey(apiKey);

            if (user.isPresent()) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    return chain.filter(
                            exchange.mutate().request(
                                            exchange.getRequest().mutate()
                                                    .header("User", objectMapper.writeValueAsString(user.get()))
                                                    .header("Source", "Rest-Api")
                                                    .header("Request-ID", requestId)
                                                    .build())
                                    .build());
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    Sentry.captureException(e, scope -> scope.setLevel(SentryLevel.ERROR));
                    return exchange.getResponse().setComplete();
                }
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        log.info("not api call");

        if (uri.matches("^/|^/status$|^/users/google-auth.*|^/users/logout.*|^/users/current.*")) {
            log.info("user auth call");
            return chain.filter(
                    exchange.mutate().request(
                                    exchange.getRequest().mutate()
                                            .header("Source", "React-App")
                                            .header("Request-ID", requestId)
                                            .build())
                            .build());
        }

        HttpCookie cookie = exchange.getRequest().getCookies().getFirst(cookieName);

        if (cookie == null) {
            log.info("no JWT cookie");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        Optional<AuthUserDTO> user = authService.decodeAndValidateJWT(cookie.getValue());
        log.info(user.toString());
        if (user.isPresent()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                log.info("Here's the user: " + user.get());
                return chain.filter(
                        exchange.mutate().request(
                                        exchange.getRequest().mutate()
                                                .header("User", objectMapper.writeValueAsString(user.get()))
                                                .header("Source", "React-App")
                                                .header("Request-ID", requestId)
                                                .build())
                                .build());
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                Sentry.captureException(e, scope -> scope.setLevel(SentryLevel.ERROR));
                return exchange.getResponse().setComplete();
            }
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private String getCookieName() {
        return profileManager.executeOnProfile(
                () -> "develop-jwt",
                () -> "testing-jwt",
                () -> "staging-jwt",
                () -> "__Secure-jwt"
        );
    }

    private ResponseCookie developLogout() {
        return ResponseCookie.from("develop-jwt")
                .value(null)
                .httpOnly(true)
                .path("/")
                .domain("localhost")
                .secure(true)
                .maxAge(Duration.ofSeconds(1))
                .sameSite("None")
                .build();
    }

    private ResponseCookie testingLogout() {
        return ResponseCookie.from("testing-jwt")
                .value(null)
                .httpOnly(true)
                .path("/")
                .secure(true)
                .maxAge(Duration.ofSeconds(1))
                .sameSite("None")
                .build();
    }

    private ResponseCookie stagingLogout() {
        return ResponseCookie.from("staging-jwt")
                .value(null)
                .httpOnly(true)
                .path("/")
                .domain("api.avogador.staging.mostserene.eu")
                .secure(true)
                .maxAge(Duration.ofSeconds(1))
                .sameSite("None")
                .build();
    }

    private ResponseCookie productionLogout() {
        return ResponseCookie.from("__Secure-jwt")
                .value(null)
                .httpOnly(true)
                .path("/")
                // .domain("api.avogador.mostserene.eu")
                .secure(true)
                .maxAge(Duration.ofSeconds(1))
                .sameSite("Lax")
                .build();
    }

    private boolean isCSRF(ServerHttpRequest request) {
        String cookieName = getCookieName();
        HttpCookie jwtCookie = request.getCookies().getFirst(cookieName);

        if (jwtCookie == null) return false;

        String jwtHash = Hashing.sha256()
                .hashString(jwtCookie.getValue(), StandardCharsets.UTF_8)
                .toString();

        String jwtSubHash = jwtHash.substring(jwtHash.length() - 20);
        log.info(request.getHeaders().getFirst("Jwt-CSRF-Hash") + " " + jwtSubHash);

        return !jwtSubHash.equals(request.getHeaders().getFirst("Jwt-CSRF-Hash"));
    }
}