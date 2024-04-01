package eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions;


import eu.mostserene.avogador.exerciseservice.projectservice.projects.Project;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Entity
@Table(name = "ProjectSubmissions")
public class ProjectSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private Project project;

    @Setter
    @NotNull
    private UUID userId;

    @Setter
    @NotNull
    private Date timestamp = new Date();

    @Setter
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProjectStatus status = ProjectStatus.PENDING;

    public ProjectSubmission() {
    }

    public ProjectSubmission(Project project, UUID userId) {
        this.project = project;
        this.userId = userId;
    }
}
