package eu.mostserene.avogador.filesystemservice.exercises;

import eu.mostserene.avogador.filesystemservice.strox.Strox;

import java.io.File;
import java.util.UUID;

public interface ExerciseStorage {
    void create();

    File get();

    void saveTemplate(Strox template);

    Strox getTemplate();

    void saveSubmission(UUID submissionId, Strox submission);

    Strox getSubmissionStrox(UUID submissionId);

    File getSubmissionCode(UUID submissionId);

    void delete();
}
