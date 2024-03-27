package eu.mostserene.avogador.exerciseservice.projectservice.notebookprojects;

import eu.mostserene.avogador.exerciseservice.projectservice.projects.Project;
import eu.mostserene.avogador.exerciseservice.projectservice.projects.ProjectType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "NotebookProjects")
public class NotebookProject extends Project {
    @NotNull
    @Enumerated(EnumType.STRING)
    private NotebookKernel kernel;

    @Override
    public ProjectType getProjectType() {
        return ProjectType.NOTEBOOK;
    }

    public NotebookProject() {
    }

    public NotebookProject(UUID courseId, String name, String description, Boolean canSubmit, Date deadline) {
        super(courseId, name, description, canSubmit, deadline);
    }
}
