package eu.mostserene.avogador.exerciseservice.projectservice.projects;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.UUID;

@Getter
@Entity
@Table(name = "Projects")
@Inheritance(strategy = InheritanceType.JOINED)
abstract public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @NotNull
    private UUID courseId;

    @Setter
    @NotNull
    private String name;

    @Setter
    @NotNull
    @Length(max = 10000)
    private String description = "";

    @Setter
    @NotNull
    @ColumnDefault("false")
    private Boolean canSubmit = false;

    @NotNull
    private Date deadline;

    public Project() {
    }

    public Project(UUID courseId, String name, String description, Boolean canSubmit, Date deadline) {
        this.courseId = courseId;
        this.name = name;
        this.description = description;
        this.canSubmit = canSubmit;
        this.deadline = deadline;
    }

    public abstract ProjectType getProjectType();
}
