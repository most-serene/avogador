package eu.mostserene.avogador.storageservice.projects;

import lombok.Data;

import java.util.UUID;

@Data
public class ProjectDTO {
    private UUID courseId;
    private UUID projectId;

    public ProjectDTO() {
    }

    public ProjectDTO(UUID courseId, UUID projectId) {
        this.courseId = courseId;
        this.projectId = projectId;
    }
}