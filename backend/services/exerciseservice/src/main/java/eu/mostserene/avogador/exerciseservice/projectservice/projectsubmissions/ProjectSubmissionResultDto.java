package eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions;

import lombok.Data;

import java.util.UUID;

@Data
public class ProjectSubmissionResultDto {
    private UUID id;
    private ProjectStatus status;

    public ProjectSubmissionResultDto() {
    }

}
