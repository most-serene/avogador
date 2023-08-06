package eu.mostserene.avogador.courseservice.usercourses;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "You are not part of this course")
public class NotMemberException extends RuntimeException{
    NotMemberException(){
        super("You are not part of this course");
    }
}
