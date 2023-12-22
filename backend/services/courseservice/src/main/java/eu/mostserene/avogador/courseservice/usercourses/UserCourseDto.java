package eu.mostserene.avogador.courseservice.usercourses;

import lombok.Data;

import java.util.UUID;

@Data
public class UserCourseDto {
    private UUID id;
    private UUID userId;
    private UUID courseId;
    private CourseRole role;

    public UserCourseDto() {
    }

    public UserCourseDto(UUID id, UUID userId, UUID courseId, CourseRole role) {
        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.role = role;
    }
}
