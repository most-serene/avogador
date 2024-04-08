package eu.mostserene.avogador.exerciseservice.security;

import eu.mostserene.avogador.exerciseservice.users.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Forbidden")
@Slf4j
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(UserDto user) {
        super("Forbidden");
        log.info("Forbidden access caught from " + user.getEmail() +
                " (" + user.getGivenName() + " " + user.getFamilyName() + ") ");
    }

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(UserDto user, String message) {
        super(message);
        log.info("Forbidden access caught from " + user.getEmail() +
                " (" + user.getGivenName() + " " + user.getFamilyName() + "): \n" +
                message
        );
    }
}
