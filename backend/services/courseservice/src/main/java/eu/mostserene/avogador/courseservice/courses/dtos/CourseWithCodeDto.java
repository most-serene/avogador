package eu.mostserene.avogador.courseservice.courses.dtos;

import eu.mostserene.avogador.courseservice.courses.Course;
import lombok.Data;

@Data
public class CourseWithCodeDto {
    private Long id;
    private String name;
    private String year;
    private Boolean isArchived;
    private String joinCode;

    public CourseWithCodeDto(Course course, String code) {
        this.id = course.getId();
        this.name = course.getName();
        this.year = course.getYear();
        this.isArchived = course.getIsArchived();
        this.joinCode = code;
    }

    public CourseWithCodeDto(Course course) {
        this(course, null);
    }
}
