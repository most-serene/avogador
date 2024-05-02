package eu.mostserene.avogador.exerciseservice.projectservice.userproject;

import eu.mostserene.avogador.exerciseservice.projectservice.projects.Project;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
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

    @Setter
    private Integer mark;

    public UserProject() {
    }

    public UserProjectDto getUserProjectDetail(UserDto userDto) {
        return new UserProjectDto(this, userDto);
    }

    public UserProject(UUID userId, Project project) {
        this.userId = userId;
        this.project = project;
    }

    public UserProject(UUID userId, Project project, Integer mark) {
        this.userId = userId;
        this.project = project;
        this.mark = mark;
    }
}
