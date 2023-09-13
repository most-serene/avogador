package eu.mostserene.avogador.courseservice.courses;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(
        name = "Courses",
        uniqueConstraints = @UniqueConstraint(columnNames={"name", "year"})
)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private String year;

    @NotNull
    private Boolean isArchived = false;

    public Course() {
    }

    public Course(String name, String year, Boolean isArchived) {
        this.name = name;
        this.year = year;
        this.isArchived = isArchived;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean archived) {
        isArchived = archived;
    }
}
