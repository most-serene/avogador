package eu.mostserene.avogador.executorservice.storage;

import eu.mostserene.avogador.executorservice.projectsubmission.ProjectSubmission;
import eu.mostserene.avogador.executorservice.submission.CodingSubmission;

import java.io.File;

public interface StorageService {

    File fetchAndSaveSubmissionCode(CodingSubmission codingSubmission);

    File fetchAndSaveTestcases(CodingSubmission codingSubmission);

    File fetchAndSaveProject(ProjectSubmission projectSubmission);

    void uploadNotebookExecutionLog(ProjectSubmission projectSubmission, File executionLog);

    void uploadNotebookReport(ProjectSubmission projectSubmission, File report);
}
