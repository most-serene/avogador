package eu.mostserene.avogador.exerciseservice.usertrials;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserTrialRepository extends JpaRepository<UserTrial, UUID> {
    List<UserTrial> findByUserId(UUID userId);
    List<UserTrial> findByTrial_Id(UUID id);
}
