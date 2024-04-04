package eu.mostserene.avogador.exerciseservice.projectservice.projects;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectRepository repository;

    @Override
    public Optional<Project> getProjectById(UUID projectId) {
        return repository.findById(projectId);
    }
}
