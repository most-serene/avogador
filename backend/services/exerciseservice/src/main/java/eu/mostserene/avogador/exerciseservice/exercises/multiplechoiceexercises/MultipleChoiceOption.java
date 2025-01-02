package eu.mostserene.avogador.exerciseservice.exercises.multiplechoiceexercises;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Entity
@Getter
@Table(name = "MultipleChoiceQuestions")
public class MultipleChoiceOption {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @NotNull
    @ManyToOne
    @JoinColumn(name = "exercise_id", referencedColumnName = "id", nullable = false)
    private MultipleChoiceExercise exercise;

    @Setter
    @NotNull
    @Length(max = 10000)
    private String label;

    @Setter
    @NotNull
    private Boolean isCorrect;

    @Setter
    @NotNull
    @Min(0)
    private Integer index;

    public MultipleChoiceOption() {

    }

    public MultipleChoiceOption(MultipleChoiceExercise exercise, String label, Boolean isCorrect, Integer index) {
        this.exercise = exercise;
        this.label = label;
        this.isCorrect = isCorrect;
        this.index = index;
    }
}
