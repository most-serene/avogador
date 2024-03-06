package eu.mostserene.avogador.exerciseservice.usertrials;

import eu.mostserene.avogador.exerciseservice.trials.Trial;
import eu.mostserene.avogador.exerciseservice.users.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserTrialService {
    Optional<UserTrial> getUserTrial(Trial trial, UserDto user);

    List<UserTrial> getUsersFromTrial(Trial trial);

    List<UserTrial> getTrialsFromUser(UserDto user);

    UserTrial joinTrial(UserDto user, Trial trial);

    UserTrial createUserTrial(UserTrial userTrial);

    void deleteUserTrialsByTrial(Trial trial);
}
