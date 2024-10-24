package eu.mostserene.avogador.exerciseservice.testcases;

import eu.mostserene.avogador.exerciseservice.exercises.codingexercises.CodingExercise;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestcaseService {
    Optional<Testcase> getSimpleTestcase(UUID testcaseId);

    Optional<TestcaseDetailDto> getTestcase(CodingExercise exercise, UUID testcaseId);

    List<TestcaseDetailDto> getTestcasesFromExercise(CodingExercise exercise) throws IllegalStateException;

    List<Testcase> getSimpleTestcasesFromExercise(CodingExercise exercise);

    TestcaseDetailDto createTestcase(TestcaseDetailDto testcase, CodingExercise exercise);

    Testcase updateTestcaseIndex(Testcase testcase, int index);

    TestcaseDetailDto updateTestcase(Testcase testcase, TestcaseDetailDto testcaseDto);

    void deleteTestcase(CodingExercise exercise, UUID testcase);

    void deleteTestcases(CodingExercise exercise);
}
