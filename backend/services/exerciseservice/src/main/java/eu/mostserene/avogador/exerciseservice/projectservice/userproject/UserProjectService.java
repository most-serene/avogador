package eu.mostserene.avogador.exerciseservice.projectservice.userproject;

import eu.mostserene.avogador.exerciseservice.projectservice.projects.Project;
import eu.mostserene.avogador.exerciseservice.users.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserProjectService {

    Optional<UserProject> getUserProject(Project project, UserDto user);

    List<UserProject> getUsersFromProject(Project project);

    UserProject joinProject(UserDto user, Project project);

    UserProject uploadMark(UserDto user, Project project, Integer mark);
}
