package eu.mostserene.avogador.apigateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import eu.mostserene.avogador.apigateway.utils.LoggerColors;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @NonNull
    @Order(5)
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        try {
            return foo(exchange, chain);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            Sentry.captureException(e, scope -> scope.setLevel(SentryLevel.ERROR));
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
        if (request.getURI().getPath().matches("^/ws.*")) {
            return false;
        }

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

    private Mono<Void> handleCSRF(ServerWebExchange exchange) {
        log.info(LoggerColors.warn(" -- CSRF detected?!"));
        ResponseCookie cookie = profileManager.executeOnProfile(
                this::developLogout,
                this::testingLogout,
                this::stagingLogout,
                this::productionLogout
        );

        exchange.getResponse().addCookie(cookie);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private Mono<Void> foo(ServerWebExchange exchange, WebFilterChain chain) throws JsonProcessingException {
        String uri = exchange.getRequest().getURI().getPath();
        String requestId = RandomStringUtils.randomAlphanumeric(10);
        String cookieName = getCookieName();

        if (isCSRF(exchange.getRequest())) {
            return handleCSRF(exchange);
        }

        if (isApiCall(uri)) {
            return handleApiCall(exchange, chain, requestId, cookieName);
        }

        if (isAuthenticationCall(uri)) {
            return handleAuthenticationCall(exchange, chain, requestId);
        }

        HttpCookie cookie = exchange.getRequest().getCookies().getFirst(cookieName);

        if (cookie == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        Optional<AuthUserDTO> user = authService.decodeAndValidateJWT(cookie.getValue());
        if (user.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return getSuccessfulChain(exchange, chain, user.get(), requestId);
    }

    private boolean isAuthenticationCall(String uri) {
        return uri.matches("^/|^/status$|^/users/google-auth.*|^/users/logout.*|^/users/current.*|^/version/webapp");
    }

    private boolean isApiCall(String uri) {
        return uri.matches("^/api.*") && !uri.matches("^/api/.*/api-key$");
    }

    private Mono<Void> getSuccessfulChain(ServerWebExchange exchange, WebFilterChain chain, AuthUserDTO user, String requestId) throws JsonProcessingException {
        return chain.filter(
                exchange.mutate().request(
                                exchange.getRequest().mutate()
                                        .header("User", objectMapper.writeValueAsString(user))
                                        .header("Source", "React-App")
                                        .header("Request-ID", requestId)
                                        .build())
                        .build());
    }

    private Mono<Void> handleAuthenticationCall(ServerWebExchange exchange, WebFilterChain chain, String requestId) {
        return chain.filter(
                exchange.mutate().request(
                                exchange.getRequest().mutate()
                                        .header("Source", "React-App")
                                        .header("Request-ID", requestId)
                                        .build())
                        .build());
    }

    private Mono<Void> handleApiCall(ServerWebExchange exchange, WebFilterChain chain, String requestId, String cookieName) throws JsonProcessingException {
        if (exchange.getRequest().getCookies().getFirst(cookieName) != null) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return exchange.getResponse().setComplete();
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !"Bearer".equals(authHeader.split("\\s+")[0]) ||
                authHeader.split("\\s+")[1] == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String apiKey = authHeader.split("\\s+")[1];
        Optional<AuthUserDTO> user = authService.decodeAndValidateApiKey(apiKey);

        if (user.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(
                exchange.mutate().request(
                                exchange.getRequest().mutate()
                                        .header("User", objectMapper.writeValueAsString(user.get()))
                                        .header("Source", "Rest-Api")
                                        .header("Request-ID", requestId)
                                        .build())
                        .build());
    }
}