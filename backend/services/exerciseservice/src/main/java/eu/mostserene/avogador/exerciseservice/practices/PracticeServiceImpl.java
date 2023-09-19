package eu.mostserene.avogador.exerciseservice.practices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PracticeServiceImpl implements PracticeService {

    @Autowired
    private PracticeRepository practiceRepository;

    @Override
    public Optional<Practice> getPractice(UUID practiceId) {
        return practiceRepository.findById(practiceId);
    }

    @Override
    public Practice createOrUpdatePractice(Practice practice) {
        return practiceRepository.save(practice);
    }
}
