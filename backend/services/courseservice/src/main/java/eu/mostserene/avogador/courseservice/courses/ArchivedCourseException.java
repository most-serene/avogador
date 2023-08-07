package eu.mostserene.avogador.courseservice.courses;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "This course is archived")
public class ArchivedCourseException extends RuntimeException{

}
