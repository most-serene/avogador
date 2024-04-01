package eu.mostserene.avogador.executorservice.executor.notebooks;

import com.github.dockerjava.api.DockerClient;
import eu.mostserene.avogador.executorservice.projectsubmission.ProjectSubmission;
import eu.mostserene.avogador.executorservice.projectsubmission.ProjectSubmissionStatus;
import eu.mostserene.avogador.executorservice.projectsubmission.ProjectType;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;

public interface NotebookKernel {
    String getName();

    ProjectType getProjectType();

    Pair<ProjectSubmissionStatus, File> runNotebook(DockerClient dockerClient, ProjectSubmission projectSubmission, File notebook);

    File generateHtmlReport(DockerClient dockerClient, ProjectSubmission projectSubmission, File notebook, File submissionFolder);
}
