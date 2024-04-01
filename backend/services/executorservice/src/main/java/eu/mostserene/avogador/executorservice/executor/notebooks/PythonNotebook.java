package eu.mostserene.avogador.executorservice.executor.notebooks;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import eu.mostserene.avogador.executorservice.executor.SandboxesUtils;
import eu.mostserene.avogador.executorservice.projectsubmission.ProjectSubmission;
import eu.mostserene.avogador.executorservice.projectsubmission.ProjectSubmissionStatus;
import eu.mostserene.avogador.executorservice.projectsubmission.ProjectType;
import eu.mostserene.avogador.executorservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class PythonNotebook implements NotebookKernel {
    @Override
    public String getName() {
        return "PYTHON";
    }

    @Override
    public ProjectType getProjectType() {
        return ProjectType.NOTEBOOK_PYTHON3;
    }

    @Override
    public Pair<ProjectSubmissionStatus, File> runNotebook(DockerClient dockerClient, ProjectSubmission projectSubmission, File notebookFolder) {
        log.info(LoggerColors.cyan("Executing " + projectSubmission.getId()));

        File notebook = findNotebook(notebookFolder);
        var container = dockerClient.createContainerCmd("gotti27/j-check-env")
                .withImage("gotti27/j-check-env")
                .withCmd("/bin/bash", "-c",
                        "PATH=$PATH:/home/student/.local/bin/ jupyter-execute --kernel_name=python3 --timeout=180 /execution/" + notebook.getName() + "; echo $?")
                //.withNetworkDisabled(true)
                .exec();

        Arrays.stream(Objects.requireNonNull(notebook.getParentFile().listFiles()))
                .forEach(file -> dockerClient.copyArchiveToContainerCmd(container.getId())
                        .withHostResource(file.toPath().toString())
                        .withCopyUIDGID(true)
                        .withRemotePath("/execution/")
                        .exec());

        dockerClient.startContainerCmd(container.getId()).exec();

        File executionReport = new File(notebookFolder.getParentFile() + "/exec.out");

        try (OutputStream fileOut = new FileOutputStream(executionReport)) {
            SandboxesUtils.waitContainer(dockerClient, container.getId());
            String containerStdout = SandboxesUtils.writeContainerLog(dockerClient, container.getId(), true, false);
            String containerStderr = SandboxesUtils.writeContainerLog(dockerClient, container.getId(), false, true);
            log.info(LoggerColors.success(containerStdout));
            log.info(LoggerColors.error(containerStderr));

            fileOut.write(containerStderr.getBytes());
            return Pair.of(
                    Integer.parseInt(containerStdout.trim()) == 0 ?
                            ProjectSubmissionStatus.SUCCESS : ProjectSubmissionStatus.ERROR,
                    executionReport
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            dockerClient.removeContainerCmd(container.getId()).exec();
        }
    }

    private File findNotebook(File notebookFolder) {
        List<File> notebooks = FileUtils.listFiles(notebookFolder, new String[]{"ipynb"}, true).stream()
                .filter(file -> file.getName().charAt(0) != '.')
                .toList();
        if (notebooks.size() != 1) {
            throw new IllegalStateException("No notebook or more than one");
        }
        File notebook = notebooks.get(0);
        File renamed = new File(notebook.getParent() + "/notebook.ipynb");
        if (!notebook.renameTo(renamed)) {
            throw new RuntimeException("An error occurred while renaming the notebook file");
        }
        return renamed;
    }

    @Override
    public File generateHtmlReport(DockerClient dockerClient, ProjectSubmission projectSubmission, File notebookFolder, File submissionFolder) {
        File notebook = findNotebook(notebookFolder);

        CreateContainerResponse container = dockerClient.createContainerCmd("gotti27/j-check-env:latest")
                .withImage("gotti27/j-check-env:latest")
                .withCmd("/bin/bash", "-c",
                        "PATH=$PATH:/home/student/.local/bin/ jupyter nbconvert --ExecutePreprocessor.kernel_name=python3 --execute --to notebook --inplace /execution/" + notebook.getName() + "; " +
                                "PATH=$PATH:/home/student/.local/bin/ jupyter nbconvert --ExecutePreprocessor.kernel_name=python3 --to html /execution/" + notebook.getName())
                // .withNetworkDisabled(true)
                .exec();

        Arrays.stream(Objects.requireNonNull(notebook.getParentFile().listFiles()))
                .forEach(file -> dockerClient.copyArchiveToContainerCmd(container.getId())
                        //.withHostResource((new File(sourceCode.getParentFile() + "/code")).toPath().toString())
                        .withHostResource(file.toPath().toString())
                        .withCopyUIDGID(true)
                        .withRemotePath("/execution/")
                        .exec());

        dockerClient.startContainerCmd(container.getId()).exec();

        try {
            SandboxesUtils.waitContainer(dockerClient, container.getId());
            String containerStdErr = SandboxesUtils.writeContainerLog(dockerClient, container.getId(), false, true);
            log.info(containerStdErr);

            File reportFile = new File(submissionFolder + "/notebook.html");
            try (InputStream inputStream = dockerClient.copyArchiveFromContainerCmd(container.getId(), "/execution/notebook.html")
                    .exec()) {
                FileUtils.copyInputStreamToFile(inputStream, reportFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return reportFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            dockerClient.removeContainerCmd(container.getId()).exec();
        }
    }
}
