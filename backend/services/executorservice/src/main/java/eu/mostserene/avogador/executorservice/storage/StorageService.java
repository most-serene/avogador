package eu.mostserene.avogador.executorservice.storage;

import eu.mostserene.avogador.executorservice.submission.Submission;

import java.io.File;

public interface StorageService {

    File fetchAndSaveSubmissionCode(Submission submission);

    File fetchAndSaveTestcases(Submission submission);
}
