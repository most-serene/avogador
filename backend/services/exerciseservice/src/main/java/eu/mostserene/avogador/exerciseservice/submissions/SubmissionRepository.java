package eu.mostserene.avogador.exerciseservice.submissions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    Optional<Submission> findFirstByExercise_IdAndUserIdOrderByTimestampDesc(UUID id, UUID userId);
    List<Submission> findByExercise_IdAndUserIdOrderByTimestampDesc(UUID id, UUID userId);
}
