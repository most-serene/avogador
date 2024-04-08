package eu.mostserene.avogador.exerciseservice.projectservice.notebookprojects;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class NotebookProjectServiceImpl implements NotebookProjectService {

    @Autowired
    private NotebookProjectRepository repository;

    @Override
    public Optional<NotebookProject> getNotebookProject(UUID projectId) {
        return repository.findById(projectId);
    }
}
