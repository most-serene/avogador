package eu.mostserene.avogador.exerciseservice.testcases;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestcaseService {

    Optional<TestcaseDetailDto> getTestcase(UUID testcaseId);
    List<TestcaseDetailDto> getTestcasesFromExercise(Exercise exercise) throws IllegalStateException;
    List<Testcase> getSimpleTestcasesFromExercise(Exercise exercise);
    TestcaseDetailDto createTestcase(TestcaseDetailDto testcase, Exercise exercise);
    Testcase updateTestcaseIndex(Testcase testcase, int index);
    TestcaseDetailDto updateTestcase(TestcaseDetailDto testcase);

}
