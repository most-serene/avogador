package eu.mostserene.avogador.exerciseservice.usertrials;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserTrialServiceImpl implements UserTrialService{
    @Autowired
    private UserTrialRepository repository;

    @Override
    public List<UserTrial> getUsersFromTrialId(UUID trialId) {
        return repository.findByTrial_Id(trialId);
    }

    @Override
    public List<UserTrial> getTrialsFromUserId(UUID userId) {
        return repository.findByUserId(userId);
    }
}
