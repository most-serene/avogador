package eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface ProjectSubmissionRepository extends JpaRepository<ProjectSubmission, UUID> {
}
