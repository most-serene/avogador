package eu.mostserene.avogador.exerciseservice.testcases;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Getter
@Entity
@Table(name = "Testcases")
public class Testcase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @NotNull
    @ManyToOne
    @JoinColumn(name = "exercise_id", referencedColumnName = "id", updatable = false, nullable = false)
    private Exercise exercise;

    @Setter
    @NotNull
    private Boolean isVisible = false;

    @Setter
    @NotNull
    @Min(0)
    private Integer index;

    @Setter
    @NotNull
    @Min(0)
    @ColumnDefault("1.0")
    private Double points = 1.;

    @Setter
    private String name;

    public Testcase() {
    }

    public Testcase(Exercise exercise, Boolean isVisible, Integer index) {
        this.exercise = exercise;
        this.isVisible = isVisible;
        this.index = index;
    }

    public Testcase(Exercise exercise, Boolean isVisible, Integer index, Double points, String name) {
        this(exercise, isVisible, index);
        this.points = points;
        this.name = name;
    }

    public TestcaseDetailDto toDetailDto(String input, String output) {
        return new TestcaseDetailDto(id, exercise.getId(), isVisible, index, input, output);
    }
}
