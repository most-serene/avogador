package eu.mostserene.avogador.exerciseservice.trials;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TrialServiceImpl implements TrialService {
    @Autowired
    private TrialRepository repository;

    @Override
    public Optional<Trial> getTrialById(UUID trialId) {
        return repository.findById(trialId);
    }

    @Override
    public void deleteTrial(Trial trial) {
        repository.deleteById(trial.getId());
    }

    @Override
    public List<Trial> getTrialsByCourseId(UUID courseId) {
        return repository.findByCourseId(courseId);
    }
}
