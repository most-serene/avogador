package eu.mostserene.avogador.exerciseservice.practices;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PracticeRepository extends JpaRepository<Practice, UUID> {
}
