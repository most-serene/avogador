package eu.mostserene.avogador.exerciseservice.trials;

import java.util.Optional;
import java.util.UUID;

public interface TrialService {
    Optional<Trial> getTrialById(UUID trialId);
}
