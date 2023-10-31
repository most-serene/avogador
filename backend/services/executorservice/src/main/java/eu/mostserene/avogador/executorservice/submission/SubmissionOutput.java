package eu.mostserene.avogador.executorservice.submission;

import lombok.Data;
import java.util.UUID;

@Data
public class SubmissionOutput {
    private UUID courseId;
    private UUID trialId;
    private UUID exerciseId;
    private UUID submissionId;
    private String testcaseId;
    private String executionOutput;

    public SubmissionOutput() {
    }

    public SubmissionOutput(Submission submission, String testcaseId, String executionOutput) {
        this.courseId = submission.getCourseId();
        this.trialId = submission.getTrialId();
        this.exerciseId = submission.getExerciseId();
        this.submissionId = submission.getId();
        this.testcaseId = testcaseId;
        this.executionOutput = executionOutput;
    }

    public SubmissionOutput(UUID courseId, UUID trialId, UUID exerciseId, UUID submissionId, String testcaseId, String executionOutput) {
        this.courseId = courseId;
        this.trialId = trialId;
        this.exerciseId = exerciseId;
        this.submissionId = submissionId;
        this.testcaseId = testcaseId;
        this.executionOutput = executionOutput;
    }
}
