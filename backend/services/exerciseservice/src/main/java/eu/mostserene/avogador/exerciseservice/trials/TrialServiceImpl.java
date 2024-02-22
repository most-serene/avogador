package eu.mostserene.avogador.exerciseservice.trials;

import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class TrialServiceImpl implements TrialService {
    @Autowired
    private TrialRepository repository;
    @Autowired
    private StorageService storageService;

    @Override
    public Optional<Trial> getTrialById(UUID trialId) {
        return repository.findById(trialId);
    }

    @Override
    public void deleteTrial(Trial trial) {
        storageService.deleteTrial(trial);
        repository.deleteById(trial.getId());
    }

    @Override
    public List<Trial> getTrialsByCourseId(UUID courseId, Boolean includeHidden) {
        if (includeHidden) {
            return repository.findByCourseId(courseId);
        }
        return repository.findByCourseIdAndIsVisibleTrue(courseId);
    }
}
