package eu.mostserene.avogador.exerciseservice.testcases;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TestcaseServiceImpl implements TestcaseService{
    @Autowired
    private TestcaseRepository repository;

    @Override
    public Optional<TestcaseDetailDto> getTestcase(UUID testcaseId) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public List<TestcaseDetailDto> getTestcasesFromExercise(Exercise exercise) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public List<Testcase> getSimpleTestcasesFromExercise(Exercise exercise) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public TestcaseDetailDto createTestcase(TestcaseDetailDto testcase) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public Testcase updateTestcaseIndex(Testcase testcase, int index) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public TestcaseDetailDto updateTestcase(TestcaseDetailDto testcase) {
        throw new NotImplementedException("Not implemented");
    }
}
