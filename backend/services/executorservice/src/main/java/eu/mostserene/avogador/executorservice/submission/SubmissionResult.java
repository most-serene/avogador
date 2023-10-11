package eu.mostserene.avogador.executorservice.submission;

import lombok.Data;

import java.util.UUID;

@Data
public class SubmissionResult {
    private UUID submissionId;
    private SubmissionStatus status;
    private UUID testcaseId;

    public SubmissionResult() {
    }

    public SubmissionResult(Submission submission, UUID testcaseId) {
        this.submissionId = submission.getId();
        this.status = SubmissionStatus.PENDING;
        this.testcaseId = testcaseId;
    }

    public SubmissionResult(UUID submissionId, UUID testcaseId, SubmissionStatus status) {
        this.submissionId = submissionId;
        this.status = status;
        this.testcaseId = testcaseId;
    }
}
