package eu.mostserene.avogador.exerciseservice.courses;

import lombok.Data;

import java.util.UUID;

@Data
public class CourseDto {
    private UUID id;
    private String name;
    private String year;
    private Boolean isArchived;

    public CourseDto() {
    }
}
