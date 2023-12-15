package eu.mostserene.avogador.courseservice.usercourses;

import eu.mostserene.avogador.courseservice.users.UserDto;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class UserCourseDetailDto {
    private UUID id;
    private UserDto user;
    private UUID courseId;
    private CourseRole role;
    private Date joinDate;

    public UserCourseDetailDto() {
    }

    public UserCourseDetailDto(UUID id, UserDto user, UUID courseId, CourseRole role, Date joinDate) {
        this.id = id;
        this.user = user;
        this.courseId = courseId;
        this.role = role;
        this.joinDate = joinDate;
    }
}
