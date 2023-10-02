package eu.mostserene.avogador.exerciseservice.testcases;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TestcaseRepository extends JpaRepository<Testcase, UUID> {
}
