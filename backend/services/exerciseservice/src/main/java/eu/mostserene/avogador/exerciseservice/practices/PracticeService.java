package eu.mostserene.avogador.exerciseservice.practices;

import java.util.Optional;
import java.util.UUID;

public interface PracticeService {
    Optional<Practice> getPractice(UUID practiceId);

    Practice createPractice(Practice practice);

    Practice updatePractice(Practice practice);

    void deletePractice(Practice practice);
}
