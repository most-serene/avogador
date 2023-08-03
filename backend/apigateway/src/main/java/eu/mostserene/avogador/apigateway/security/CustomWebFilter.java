package eu.mostserene.avogador.apigateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

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
}