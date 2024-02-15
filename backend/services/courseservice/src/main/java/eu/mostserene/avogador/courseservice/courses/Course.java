package eu.mostserene.avogador.courseservice.courses;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(
        name = "Courses",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "year"})
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

    public void setName(String name) {
        this.name = name;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setIsArchived(Boolean archived) {
        isArchived = archived;
    }

    /**
     * Require the course to not be archived
     *
     * @return the course itself if it has not been archived
     * @throws ArchivedCourseException if the course has been archived
     */
    public Course requireNotArchived() {
        if (this.isArchived) {
            throw new ArchivedCourseException();
        }
        return this;
    }
}
