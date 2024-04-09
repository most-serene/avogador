package eu.mostserene.avogador.exerciseservice.projectservice.userproject;


import eu.mostserene.avogador.exerciseservice.projectservice.projects.Project;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class UserProjectServiceImpl implements UserProjectService {

    @Autowired
    private UserProjectRepository repository;

    @Override
    public Optional<UserProject> getUserProject(UUID projectId, UUID userId) {
        return repository.findByProject_IdAndUserId(projectId, userId);
    }

    @Override
    public UserProject joinProject(UserDto user, Project project) {
        return repository.save(new UserProject(user.getId(), project));
    }
}
