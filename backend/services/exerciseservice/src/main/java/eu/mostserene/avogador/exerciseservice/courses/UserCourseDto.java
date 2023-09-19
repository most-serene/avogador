package eu.mostserene.avogador.exerciseservice.courses;

import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Data
public class UserCourseDto {
    private UUID id;

    private UUID userId;

    private CourseDto course;

    private CourseRole role = CourseRole.STUDENT;

    private Date joinDate = new Date(Calendar.getInstance().getTimeInMillis());

    public UserCourseDto() {
    }
}
