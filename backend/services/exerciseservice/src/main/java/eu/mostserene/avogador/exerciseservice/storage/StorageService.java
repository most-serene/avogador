package eu.mostserene.avogador.exerciseservice.storage;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.strox.Strox;
import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseDetailDto;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseIODto;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import org.springframework.core.io.Resource;

import java.util.Optional;
import java.util.UUID;


public interface StorageService {
    void createTrial(Trial trial);
    void deleteTrial(Trial trial);
    void createExercise(Exercise exercise);
    void createExerciseTemplate(Exercise exercise, Strox template);
    void deleteExercise(Exercise exercise);
    void createTestcase(Exercise exercise, TestcaseDetailDto testcase);
    void deleteTestcase(TestcaseDetailDto testcase);
    Optional<TestcaseIODto> getTestcase(Exercise exercise, UUID testcaseId);
    void updateTestcase(Exercise exercise,TestcaseDetailDto testcase);
    void createSubmission(Submission submission, Strox strox);
    Optional<Strox> getSubmissionStrox(Submission submission);
    Optional<Resource> getSubmissionSource(Submission submission);
    Optional<Strox> getExerciseTemplate(Exercise exercise);
    Optional<Strox> getMergedSubmission(Submission submission);
}
