package eu.mostserene.avogador.executorservice.submission;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Submission {
    private UUID id;
    private UUID courseId;
    private UUID trialId;
    private UUID exerciseId;
    private String language;
    private String filename;

    private Integer timeLimit;
    private List<UUID> testcases;

    public Submission() {
    }
}
