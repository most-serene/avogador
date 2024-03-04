package eu.mostserene.avogador.exerciseservice.testcases;

import lombok.Data;

import java.util.UUID;

@Data
public class TestcaseDetailDto {
    private UUID id;
    private UUID exerciseId;
    private Boolean isVisible = false;
    private Integer index;
    private String input;
    private String output;
    private Double points = 1.0;
    private String name;

    public TestcaseDetailDto() {
    }

    public TestcaseDetailDto(UUID id, UUID exercise, Boolean isVisible, Integer index, String input, String output) {
        this.id = id;
        this.exerciseId = exercise;
        this.isVisible = isVisible;
        this.index = index;
        this.input = input;
        this.output = output;
    }

    public TestcaseDetailDto(UUID id, UUID exercise, Boolean isVisible, Integer index, String input, String output, Double points, String name) {
        this.id = id;
        this.exerciseId = exercise;
        this.isVisible = isVisible;
        this.index = index;
        this.input = input;
        this.output = output;
        this.points = points;
        this.name = name;
    }
}
