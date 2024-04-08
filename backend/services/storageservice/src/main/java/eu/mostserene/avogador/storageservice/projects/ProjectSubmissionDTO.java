package eu.mostserene.avogador.storageservice.projects;

import lombok.Data;

import java.util.UUID;

@Data
public class ProjectSubmissionDTO {
    private UUID courseId;
    private UUID projectId;
    private UUID submissionId;

    public ProjectSubmissionDTO() {
    }

    public ProjectSubmissionDTO(UUID courseId, UUID projectId, UUID submissionId) {
        this.courseId = courseId;
        this.projectId = projectId;
        this.submissionId = submissionId;
    }
}
