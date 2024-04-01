package eu.mostserene.avogador.exerciseservice.projectservice.userproject;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserProjectRepository extends JpaRepository<UserProject, UUID> {
}
