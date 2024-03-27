package eu.mostserene.avogador.exerciseservice.projectservice.userproject;

import eu.mostserene.avogador.exerciseservice.projectservice.projects.Project;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(
        name = "UserProjects",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "project_id"})
)
public class UserProject {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private UUID userId;

    @JoinColumn(name = "project_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private Project project;

    private Integer mark;
}
