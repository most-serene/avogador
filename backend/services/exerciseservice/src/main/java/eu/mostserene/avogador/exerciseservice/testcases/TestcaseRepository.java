package eu.mostserene.avogador.exerciseservice.testcases;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface TestcaseRepository extends JpaRepository<Testcase, UUID> {
    @Transactional
    @Modifying
    @Query("update Testcase t set t.isVisible = ?1, t.index = ?2 where t.id = ?3")
    void updateIsVisibleAndIndexById(@NonNull Boolean isVisible, @NonNull Integer index, UUID id);

    List<Testcase> findByExercise_Id(UUID id);
}
