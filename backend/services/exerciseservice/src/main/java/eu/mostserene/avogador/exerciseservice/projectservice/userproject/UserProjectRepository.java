package eu.mostserene.avogador.exerciseservice.projectservice.userproject;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProjectRepository extends JpaRepository<UserProject, UUID> {
    Optional<UserProject> findByProject_IdAndUserId(UUID projectId, UUID userId);

}
