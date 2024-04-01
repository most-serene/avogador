package eu.mostserene.avogador.executorservice.projectsubmission;

import eu.mostserene.avogador.executorservice.executor.Submission;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectSubmission extends Submission {
    private UUID projectId;
    private ProjectType projectType;

    public ProjectSubmission() {
    }
}
