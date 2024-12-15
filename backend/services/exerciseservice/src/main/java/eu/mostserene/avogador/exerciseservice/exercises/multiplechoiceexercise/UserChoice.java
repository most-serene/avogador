package eu.mostserene.avogador.exerciseservice.exercises.multiplechoiceexercise;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "UserChoices")
public class UserChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @NotNull
    private UUID userId;

    @Setter
    @JoinColumn(name = "exercise_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private MultipleChoiceExercise exercise;

    @Setter
    @JoinColumn(name = "option_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private MultipleChoiceOption option;

    public UserChoice() {
    }
}
