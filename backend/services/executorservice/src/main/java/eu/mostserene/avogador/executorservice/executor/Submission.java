package eu.mostserene.avogador.executorservice.executor;

import lombok.Data;

import java.util.UUID;

@Data
public abstract class Submission {
    private UUID id;
    private UUID courseId;
}
