package eu.mostserene.avogador.exerciseservice.exercises;

import lombok.Data;

import java.util.UUID;

@Data
public class ExerciseDto {
    private UUID id;

    private UUID trialId;

    private String name;

    private String statement;

    private Integer timeLimit;

    private Boolean isVisible;

    public ExerciseDto() {
    }

    public ExerciseDto(UUID id, UUID trialId, String name, String statement, Integer timeLimit, Boolean isVisible) {
        this.id = id;
        this.trialId = trialId;
        this.name = name;
        this.statement = statement;
        this.timeLimit = timeLimit;
        this.isVisible = isVisible;
    }
}
