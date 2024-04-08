package eu.mostserene.avogador.exerciseservice.storage;

import eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises.CodingExercise;
import eu.mostserene.avogador.exerciseservice.antiplagiarism.PlagiarismReport;
import eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions.ProjectSubmission;
import eu.mostserene.avogador.exerciseservice.strox.Strox;
import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseDetailDto;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseIODto;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface StorageService {
    void createTrial(Trial trial);

    void deleteTrial(Trial trial);

    void createExercise(CodingExercise exercise);

    void createExerciseTemplate(CodingExercise exercise, Strox template);

    void deleteExercise(CodingExercise exercise);

    void createTestcase(CodingExercise exercise, TestcaseDetailDto testcase);

    void deleteTestcase(CodingExercise exercise, UUID testcaseId);

    Optional<TestcaseIODto> getTestcase(CodingExercise exercise, UUID testcaseId);

    void updateTestcase(CodingExercise exercise, TestcaseDetailDto testcase);

    void createSubmission(Submission submission, Strox strox);

    Optional<Strox> getSubmissionStrox(Submission submission);

    Optional<Resource> getSubmissionSource(Submission submission);

    Optional<Strox> getExerciseTemplate(CodingExercise exercise);

    Optional<Strox> getMergedSubmission(Submission submission);

    Resource getExerciseLatestSubmissionsSources(CodingExercise exercise, List<UUID> submissionIds);

    void uploadSimilarityReport(CodingExercise exercise, File reportZip);

    Optional<PlagiarismReport> getSimilarityReport(CodingExercise exercise);

    // void deleteProjectSubmission(ProjectSubmission submission);

    void createProjectSubmission(ProjectSubmission submission, File file);
}
