package eu.mostserene.avogador.filesystemservice.submission;

import eu.mostserene.avogador.filesystemservice.exercises.ExerciseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubmissionOutputDto extends ExerciseDTO {
    private UUID submissionId;
    private String testcaseId;
    private String executionOutput;

    public SubmissionOutputDto() {
    }

    public SubmissionOutputDto(UUID courseId, UUID trialId, UUID exerciseId, UUID submissionId, String testcaseId, String executionOutput) {
        super(courseId, trialId, exerciseId);
        this.submissionId = submissionId;
        this.testcaseId = testcaseId;
        this.executionOutput = executionOutput;
    }
}
