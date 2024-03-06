package eu.mostserene.avogador.exerciseservice.usertrials;

import eu.mostserene.avogador.exerciseservice.trials.Trial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserTrialRepository extends JpaRepository<UserTrial, UUID> {
    Optional<UserTrial> findByTrialAndUserId(@NonNull Trial trial, @NonNull UUID userId);

    List<UserTrial> findByUserId(UUID userId);

    List<UserTrial> findByTrial_Id(UUID id);

    long deleteByTrial_Id(UUID id);
}
