package eu.mostserene.avogador.exerciseservice.usertrials;

import eu.mostserene.avogador.exerciseservice.practices.Practice;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserTrialServiceImpl implements UserTrialService {
    @Autowired
    private UserTrialRepository repository;

    @Override
    public Optional<UserTrial> getUserTrial(Trial trial, UserDto user) {
        return repository.findByTrialAndUserId(trial, user.getId());
    }

    @Override
    public List<UserTrial> getUsersFromTrial(Trial trial) {
        return repository.findByTrial_Id(trial.getId());
    }

    @Override
    public List<UserTrial> getTrialsFromUser(UserDto user) {
        return repository.findByUserId(user.getId());
    }

    @Override
    public UserTrial joinTrial(UserDto user, Trial trial) {
        var userTrial = repository.findByTrialAndUserId(trial, user.getId())
                .orElseGet(() -> new UserTrial(user.getId(), trial, false));

        if (userTrial.getStartTime() != null)
            return userTrial;

        userTrial.init();
        return repository.save(userTrial);
    }

    @Override
    public UserTrial createUserTrial(UserTrial userTrial) {
        return repository.save(userTrial);
    }
}
