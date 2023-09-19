package eu.mostserene.avogador.exerciseservice.practices;

import eu.mostserene.avogador.exerciseservice.users.UserDto;

import java.util.Optional;
import java.util.UUID;

public interface PracticeService {
    Optional<Practice> getPractice(UUID practiceId);

    Practice createPractice(Practice practice);
}
