package eu.mostserene.avogador.exerciseservice.projectservice.userproject;

import eu.mostserene.avogador.exerciseservice.projectservice.projects.Project;
import eu.mostserene.avogador.exerciseservice.users.UserDto;

import java.util.Optional;
import java.util.UUID;

public interface UserProjectService {

    Optional<UserProject> getUserProject(UUID projectId, UUID userId);

    UserProject joinProject(UserDto user, Project project);
}
