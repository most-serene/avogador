package eu.mostserene.avogador.courseservice.courses.dtos;

import eu.mostserene.avogador.courseservice.courses.Course;
import eu.mostserene.avogador.courseservice.usercourses.CourseRole;
import lombok.Data;

import java.util.UUID;

@Data
public class CourseDetailDto {
    private UUID id;
    private String name;
    private String year;
    private Boolean isArchived;
    private String joinCode;
    private CourseRole role;

    public CourseDetailDto() {
    }

    public CourseDetailDto(Course course, String code, CourseRole role) {
        this.id = course.getId();
        this.name = course.getName();
        this.year = course.getYear();
        this.isArchived = course.getIsArchived();
        this.joinCode = code;
        this.role = role;
    }

    public CourseDetailDto(Course course, CourseRole role) {
        this(course, null, role);
    }
}
