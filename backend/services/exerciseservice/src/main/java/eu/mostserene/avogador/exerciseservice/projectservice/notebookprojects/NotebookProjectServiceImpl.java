package eu.mostserene.avogador.exerciseservice.projectservice.notebookprojects;

import eu.mostserene.avogador.exerciseservice.storage.StorageService;
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

    @Autowired
    private StorageService storageService;

    @Override
    public Optional<NotebookProject> getNotebookProject(UUID projectId) {
        return repository.findById(projectId);
    }

    @Override
    public NotebookProject createProject(NotebookProject project) {
        NotebookProject savedProject = repository.save(new NotebookProject(
                project.getCourseId(),
                project.getName(),
                project.getDescription(),
                project.getCanSubmit(),
                project.getDeadline()
        ));
        storageService.createProject(savedProject);
        return savedProject;
    }

    @Override
    public NotebookProject updateProject(NotebookProject project) {
        return repository.save(project);
    }
}
