package eu.mostserene.avogador.exerciseservice.practices;

import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import eu.mostserene.avogador.exerciseservice.utils.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
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
        validateStartTimestampAndDeadline(practice);
        Practice createdPractice = practiceRepository.save(practice);
        storageService.createTrial(createdPractice);
        return createdPractice;
    }

    private void validateStartTimestampAndDeadline(Practice practice) {
        if (practice.getStartTimestamp().before(Date.from(Instant.now()))) {
            throw new BadRequestException("Trials cannot start in the past");
        }
        if (practice.getDeadline().before(practice.getStartTimestamp())) {
            throw new BadRequestException("Trials cannot end before their beginning");
        }
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
