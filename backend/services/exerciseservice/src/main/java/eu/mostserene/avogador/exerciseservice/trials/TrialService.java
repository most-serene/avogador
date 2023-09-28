package eu.mostserene.avogador.exerciseservice.trials;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrialService {
    Optional<Trial> getTrialById(UUID trialId);

    void deleteTrial(Trial trial);

    List<Trial> getTrialsByCourseId(UUID courseId, Boolean includeHidden);
}
