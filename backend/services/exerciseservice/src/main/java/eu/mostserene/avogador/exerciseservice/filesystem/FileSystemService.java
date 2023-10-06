package eu.mostserene.avogador.exerciseservice.filesystem;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseDetailDto;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseIODto;
import eu.mostserene.avogador.exerciseservice.trials.Trial;

import java.util.Optional;
import java.util.UUID;


public interface FileSystemService {
    void createTrial(Trial trial);
    void deleteTrial(Trial trial);
    void createExercise(Exercise exercise);
    void deleteExercise(Exercise exercise);
    void createTestcase(Exercise exercise, TestcaseDetailDto testcase);
    void deleteTestcase(TestcaseDetailDto testcase);
    Optional<TestcaseIODto> getTestcase(Exercise exercise, UUID testcaseId);
    void updateTestcase(Exercise exercise,TestcaseDetailDto testcase);
}
