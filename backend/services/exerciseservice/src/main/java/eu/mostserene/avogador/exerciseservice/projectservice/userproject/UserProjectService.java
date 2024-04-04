package eu.mostserene.avogador.exerciseservice.projectservice.userproject;

import java.util.Optional;
import java.util.UUID;

public interface UserProjectService {

    Optional<UserProject> getUserProject(UUID projectId, UUID userId);
}
