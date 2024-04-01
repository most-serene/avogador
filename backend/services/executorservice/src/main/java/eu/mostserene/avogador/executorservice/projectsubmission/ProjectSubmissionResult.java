package eu.mostserene.avogador.executorservice.projectsubmission;

import lombok.Data;

import java.util.UUID;

@Data
public class ProjectSubmissionResult {
    private UUID id;
    private ProjectSubmissionStatus status;

    public ProjectSubmissionResult() {
    }

    public ProjectSubmissionResult(UUID id, ProjectSubmissionStatus status) {
        this.id = id;
        this.status = status;
    }
}
