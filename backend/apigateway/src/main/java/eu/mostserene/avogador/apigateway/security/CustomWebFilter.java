package eu.mostserene.avogador.apigateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
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

@Component
@Slf4j
public class CustomWebFilter implements WebFilter {

    @Autowired
    private AuthService authService;

    @Override
    @NonNull
    @Order(5)
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String uri = exchange.getRequest().getURI().getPath();

        if (isCSRF(exchange.getRequest())) {
            ResponseCookie cookie = ResponseCookie.from("jwt", "")
                    .path("/")
                    .httpOnly(true)
                    .maxAge(Duration.ZERO)
                    .build();
            exchange.getResponse().addCookie(cookie);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        if ("/users/google-auth".equals(uri) || "/users/logout".equals(uri) || "/".equals(uri)) {
            return chain.filter(exchange);
        }

        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("jwt");

        if (cookie != null) {
            log.info(cookie.getValue());
            AuthUserDTO user = authService.decodeAndValidateJWT(cookie.getValue());
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                log.info(objectMapper.writeValueAsString(user));


                return chain.filter(
                        exchange.mutate().request(
                                        exchange.getRequest().mutate()
                                                .header("User", objectMapper.writeValueAsString(user))
                                                .build())
                                .build());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isCSRF(ServerHttpRequest request) {
        HttpCookie jwtCookie = request.getCookies().getFirst("jwt");

        if (jwtCookie == null) return false;

        String jwtHash = Hashing.sha256()
                .hashString(jwtCookie.getValue(), StandardCharsets.UTF_8)
                .toString();

        String jwtSubHash = jwtHash.substring(jwtHash.length() - 20);
        log.info(request.getHeaders().getFirst("Jwt-CSRF-Hash") + " " + jwtSubHash);

        return !jwtSubHash.equals(request.getHeaders().getFirst("Jwt-CSRF-Hash"));
    }
}