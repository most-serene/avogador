package eu.mostserene.avogador.executorservice.submission;

import lombok.Data;

import java.util.UUID;

@Data
public class Submission {
    private UUID id;
    private UUID courseId;
    private UUID trialId;
    private UUID exerciseId;
    private String language;

    public Submission() {
    }
}
