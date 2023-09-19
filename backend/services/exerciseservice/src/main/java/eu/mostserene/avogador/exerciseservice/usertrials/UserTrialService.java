package eu.mostserene.avogador.exerciseservice.usertrials;

import eu.mostserene.avogador.exerciseservice.trials.Trial;

import java.util.List;
import java.util.UUID;

public interface UserTrialService {
    List<UserTrial> getUsersFromTrialId(UUID trial);
}
