package eu.mostserene.avogador.usercourse.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException{
    public NotFoundException(String resource) {
        super(resource + " not found");
    }
    public NotFoundException() {
        super("Resource not found");
    }

}