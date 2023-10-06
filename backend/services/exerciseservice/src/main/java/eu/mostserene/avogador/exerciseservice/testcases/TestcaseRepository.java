package eu.mostserene.avogador.exerciseservice.testcases;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TestcaseRepository extends JpaRepository<Testcase, UUID> {
    List<Testcase> findByExercise_Id(UUID id);
}
