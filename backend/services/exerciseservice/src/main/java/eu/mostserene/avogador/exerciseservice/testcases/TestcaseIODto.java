package eu.mostserene.avogador.exerciseservice.testcases;

import lombok.Data;

import java.util.UUID;

@Data
public class TestcaseIODto {
    private UUID testcaseId;
    private String input;
    private String output;

    public TestcaseIODto() {
    }

    public TestcaseIODto(UUID testcaseId, String input, String output) {
        this.testcaseId = testcaseId;
        this.input = input;
        this.output = output;
    }
}