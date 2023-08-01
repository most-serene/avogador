package eu.mostserene.avogador.userservice.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Jwt cookie missing")
public class MissingJwtException extends RuntimeException {
    public MissingJwtException() {
        super("Jwt cookie missing");
    }
}