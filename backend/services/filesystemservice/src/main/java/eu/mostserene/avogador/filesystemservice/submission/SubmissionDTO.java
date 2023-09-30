package eu.mostserene.avogador.filesystemservice.submission;

import eu.mostserene.avogador.filesystemservice.exercises.ExerciseDTO;
import eu.mostserene.avogador.filesystemservice.strox.Strox;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubmissionDTO extends ExerciseDTO {
    private UUID submissionId;
    private Strox submission;

    public SubmissionDTO() {
    }

    public SubmissionDTO(UUID courseId, UUID trialId, UUID exerciseId, UUID submissionId, Strox submission) {
        super(courseId, trialId, exerciseId);
        this.submissionId = submissionId;
        this.submission = submission;
    }
}
