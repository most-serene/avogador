package eu.mostserene.avogador.exerciseservice.trials;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrialRepository extends JpaRepository<Trial, UUID> {
}
