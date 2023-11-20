package eu.mostserene.avogador.storageservice.testcases;

import eu.mostserene.avogador.storageservice.exercises.ExerciseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class TestcaseDTO extends ExerciseDTO {
    private UUID testcaseId;
    private String input;
    private String output;

    public TestcaseDTO() {
    }

    public TestcaseDTO(UUID courseId, UUID trialId, UUID exerciseId, UUID testcaseId, String input, String output) {
        super(courseId, trialId, exerciseId);
        this.testcaseId = testcaseId;
        this.input = input;
        this.output = output;
    }
}
