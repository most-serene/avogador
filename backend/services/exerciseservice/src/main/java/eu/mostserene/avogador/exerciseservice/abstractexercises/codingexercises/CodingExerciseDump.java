package eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises;

import eu.mostserene.avogador.exerciseservice.strox.Strox;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseDetailDto;
import lombok.Data;

import java.util.List;

@Data
public class CodingExerciseDump {
    private CodingExercise exercise;
    private Strox template;
    private List<TestcaseDetailDto> testcases;
}
