package eu.mostserene.avogador.filesystemservice.exercises;

import eu.mostserene.avogador.filesystemservice.strox.Strox;
import eu.mostserene.avogador.filesystemservice.testcases.TestcaseDTO;
import eu.mostserene.avogador.filesystemservice.testcases.TestcaseResponseTDO;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExerciseStorage {
    void create();

    File get();

    void saveTemplate(Strox template);

    Optional<Strox> getTemplate();

    void saveSubmission(UUID submissionId, Strox submission);

    Optional<Strox> getSubmissionStrox(UUID submissionId);

    Optional<File> getSubmissionCode(UUID submissionId);

    void saveTestcase(UUID testcaseId, String input, String output);

    Optional<TestcaseResponseTDO> getTestcase(UUID testcaseId);

    Optional<File> getTestcases();

    void delete();
}
