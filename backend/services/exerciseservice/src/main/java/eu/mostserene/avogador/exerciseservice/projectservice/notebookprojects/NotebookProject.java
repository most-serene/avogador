package eu.mostserene.avogador.exerciseservice.projectservice.notebookprojects;

import eu.mostserene.avogador.exerciseservice.projectservice.projects.Project;
import eu.mostserene.avogador.exerciseservice.projectservice.projects.ProjectType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "NotebookProjects")
public class NotebookProject extends Project {
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
