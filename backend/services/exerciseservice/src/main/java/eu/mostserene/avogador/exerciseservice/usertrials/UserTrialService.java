package eu.mostserene.avogador.exerciseservice.usertrials;

import eu.mostserene.avogador.exerciseservice.trials.Trial;
import eu.mostserene.avogador.exerciseservice.users.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserTrialService {
    List<UserTrial> getUsersFromTrial(Trial trial);
    List<UserTrial> getTrialsFromUser(UserDto user);

}
