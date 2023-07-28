package eu.mostserene.avogador.usercourse.courses;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Courses")
public class Course {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment")
    @Column(columnDefinition = "serial")
    private Long id;

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

    public Long getId() {
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
