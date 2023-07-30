package eu.mostserene.avogador.userservice.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Some parameters are invalid")
public class InvalidDomainException extends Exception {
    public InvalidDomainException() {
        super("The email domain is not @stud.unive.it or @unive.it");
    }
}
