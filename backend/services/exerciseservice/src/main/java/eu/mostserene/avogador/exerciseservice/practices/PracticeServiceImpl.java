package eu.mostserene.avogador.exerciseservice.practices;

import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PracticeServiceImpl implements PracticeService {

    @Autowired
    private PracticeRepository practiceRepository;

    @Autowired
    private StorageService storageService;

    @Override
    public Optional<Practice> getPractice(UUID practiceId) {
        return practiceRepository.findById(practiceId);
    }

    @Override
    public Practice createPractice(Practice practice) {
        Practice createdPractice = practiceRepository.save(practice);
        storageService.createTrial(createdPractice);
        return createdPractice;
    }

    @Override
    public Practice updatePractice(Practice practice) {
        return practiceRepository.save(practice);
    }

    @Override
    public void deletePractice(Practice practice) {
        practiceRepository.delete(practice);
        storageService.deleteTrial(practice);
    }
}
