package eu.mostserene.avogador.exerciseservice.projectservice.projects;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectService {
    Optional<Project> getProjectById(UUID projectId);

    List<Project> getProjectsByCourseId(UUID courseId);
}
