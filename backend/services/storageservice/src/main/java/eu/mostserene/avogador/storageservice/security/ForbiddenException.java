package eu.mostserene.avogador.storageservice.security;

import eu.mostserene.avogador.storageservice.users.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Forbidden")
@Slf4j
public class ForbiddenException extends RuntimeException{
    public ForbiddenException(UserDto user) {
        super("Forbidden");
        /*log.info(LoggerColors.error("Forbidden access caught from " + user.getEmail() +
                " (" + user.getGivenName() + " " + user.getFamilyName() + ") "));*/
        log.info("Forbidden access caught from " + user.getEmail() +
                " (" + user.getGivenName() + " " + user.getFamilyName() + ") ");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
