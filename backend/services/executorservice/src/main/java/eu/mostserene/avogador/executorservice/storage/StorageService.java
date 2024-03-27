package eu.mostserene.avogador.executorservice.storage;

import eu.mostserene.avogador.executorservice.submission.CodingSubmission;

import java.io.File;

public interface StorageService {

    File fetchAndSaveSubmissionCode(CodingSubmission codingSubmission);

    File fetchAndSaveTestcases(CodingSubmission codingSubmission);
}
