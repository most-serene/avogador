package eu.mostserene.avogador.exerciseservice.abstractexercises;

import lombok.Data;

import java.util.UUID;

@Data
public class AbstractExerciseDto {
    private UUID id;
    private UUID trialId;
    private String name;
    private String statement;
    private Boolean isVisible;

    public AbstractExerciseDto() {
    }

    public AbstractExerciseDto(UUID id, UUID trialId, String name, String statement, Boolean isVisible) {
        this.id = id;
        this.trialId = trialId;
        this.name = name;
        this.statement = statement;
        this.isVisible = isVisible;
    }
}
