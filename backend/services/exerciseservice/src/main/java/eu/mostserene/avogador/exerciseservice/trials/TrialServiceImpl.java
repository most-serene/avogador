<<<<<<< Updated upstream
package eu.mostserene.avogador.exerciseservice.trials;public class TrialServiceImpl {
=======
package eu.mostserene.avogador.exerciseservice.trials;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
>>>>>>> Stashed changes
}
