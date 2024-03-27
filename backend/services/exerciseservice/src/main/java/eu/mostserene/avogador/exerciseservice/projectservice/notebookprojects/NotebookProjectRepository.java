package eu.mostserene.avogador.exerciseservice.projectservice.notebookprojects;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotebookProjectRepository extends JpaRepository<NotebookProject, UUID> {
}
