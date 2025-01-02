package eu.mostserene.avogador.exerciseservice.exercises.multiplechoiceexercises;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode
public class MultipleChoiceOptionDto {
    private UUID id;
    private UUID exerciseId;
    private String label;
    private Boolean isCorrect;
    private Integer index;

    public MultipleChoiceOptionDto() {
    }

    public MultipleChoiceOptionDto(MultipleChoiceOption option) {
        this.id = option.getId();
        this.exerciseId = option.getExercise().getId();
        this.label = option.getLabel();
        this.isCorrect = option.getIsCorrect();
        this.index = option.getIndex();
    }
}
