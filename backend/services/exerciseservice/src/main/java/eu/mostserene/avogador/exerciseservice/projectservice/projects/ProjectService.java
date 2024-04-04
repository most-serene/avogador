package eu.mostserene.avogador.exerciseservice.projectservice.projects;

import java.util.Optional;
import java.util.UUID;

public interface ProjectService {
    Optional<Project> getProjectById(UUID projectId);
}
