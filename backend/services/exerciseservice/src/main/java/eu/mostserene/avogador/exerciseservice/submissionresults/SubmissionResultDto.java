package eu.mostserene.avogador.exerciseservice.submissionresults;

import lombok.Data;

import java.util.UUID;

@Data
public class SubmissionResultDto {
    private UUID id;
    private UUID submissionId;
    private UUID testcaseId;
    private SubmissionStatus status;
    private String output;

    public SubmissionResultDto() {
    }

    public SubmissionResultDto(UUID id, UUID submissionId, UUID testcaseId, SubmissionStatus status) {
        this.id = id;
        this.submissionId = submissionId;
        this.testcaseId = testcaseId;
        this.status = status;
    }
}
