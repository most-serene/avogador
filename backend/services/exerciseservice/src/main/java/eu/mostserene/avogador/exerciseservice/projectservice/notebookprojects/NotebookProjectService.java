package eu.mostserene.avogador.exerciseservice.projectservice.notebookprojects;


import java.util.Optional;
import java.util.UUID;

public interface NotebookProjectService {
    Optional<NotebookProject> getNotebookProject(UUID projectId);

    NotebookProject createProject(NotebookProject project);

    NotebookProject updateProject(NotebookProject project);
}
