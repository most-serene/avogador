package eu.mostserene.avogador.executorservice.submission;

import eu.mostserene.avogador.executorservice.executor.Submission;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class CodingSubmission extends Submission {
    private UUID trialId;
    private UUID exerciseId;
    private String language;
    private String filename;

    private Integer timeLimit;
    private List<UUID> testcases;

    public CodingSubmission() {
    }
}
