package eu.mostserene.avogador.exerciseservice.courses;

import lombok.Data;

import java.util.UUID;

@Data
public class CourseDetailDto {
    private UUID id;
    private String name;
    private String year;
    private Boolean isArchived;
    private CourseRole role;

    public CourseDetailDto() {
    }

    public CourseDetailDto(UUID id, String name, String year, Boolean isArchived, CourseRole role) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.isArchived = isArchived;
        this.role = role;
    }
}
