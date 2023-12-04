package eu.mostserene.avogador.storageservice.submission;

import eu.mostserene.avogador.storageservice.strox.Strox;
import lombok.Data;

import java.util.UUID;

@Data
public class SubmissionSavedDTO {
    private UUID submissionId;
    private Strox strox;

    public SubmissionSavedDTO() {
    }

    public SubmissionSavedDTO(UUID submissionId, Strox strox) {
        this.submissionId = submissionId;
        this.strox = strox;
    }
}
