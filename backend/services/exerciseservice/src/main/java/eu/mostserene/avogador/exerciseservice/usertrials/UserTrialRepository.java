package eu.mostserene.avogador.exerciseservice.usertrials;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserTrialRepository extends JpaRepository<UserTrial, UUID> {
}
