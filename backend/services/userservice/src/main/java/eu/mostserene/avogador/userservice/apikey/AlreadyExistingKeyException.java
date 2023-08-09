package eu.mostserene.avogador.userservice.apikey;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "A key with that name already exists for this user")
public class AlreadyExistingKeyException extends RuntimeException {
}
