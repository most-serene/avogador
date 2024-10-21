package eu.mostserene.avogador.exerciseservice.exercises.codingexercises;

import eu.mostserene.avogador.exerciseservice.strox.Strox;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseDetailDto;
import lombok.Data;

import java.util.List;

@Data
public class CodingExerciseDump {
    private CodingExercise exercise;
    private Strox template;
    private List<TestcaseDetailDto> testcases;

    public CodingExerciseDump(CodingExercise exercise, Strox template, List<TestcaseDetailDto> testcases) {
        this.exercise = exercise;
        this.template = template;
        this.testcases = testcases;
    }
}
