package eu.mostserene.avogador.exerciseservice.usertrials;

import eu.mostserene.avogador.exerciseservice.trials.Trial;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserTrialServiceImpl implements UserTrialService{
    @Autowired
    private UserTrialRepository repository;

    @Override
    public List<UserTrial> getUsersFromTrial(Trial trial) {
        return repository.findByTrial_Id(trial.getId());
    }

    @Override
    public List<UserTrial> getTrialsFromUser(UserDto user) {
        return repository.findByUserId(user.getId());
    }
}
