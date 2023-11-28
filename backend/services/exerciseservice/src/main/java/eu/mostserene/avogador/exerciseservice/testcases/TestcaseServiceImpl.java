package eu.mostserene.avogador.exerciseservice.testcases;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionResultService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class TestcaseServiceImpl implements TestcaseService {
    @Autowired
    private TestcaseRepository repository;
    @Autowired
    private SubmissionResultService submissionResultService;
    @Autowired
    private StorageService storageService;

    @Override
    public Optional<Testcase> getSimpleTestcase(UUID testcaseId) {
        return repository.findById(testcaseId);
    }

    @Override
    public Optional<TestcaseDetailDto> getTestcase(Exercise exercise, UUID testcaseId) {
        Optional<Testcase> testcase = repository.findById(testcaseId);

        return testcase.map(tc -> {
            Optional<TestcaseIODto> testcaseIO = storageService.getTestcase(exercise, testcaseId);
            return testcaseIO.map(testcaseIODto -> tc.toDetailDto(testcaseIODto.getInput(), testcaseIODto.getOutput()))
                    .orElse(null);
        });
    }

    @Override
    public List<TestcaseDetailDto> getTestcasesFromExercise(Exercise exercise) throws IllegalStateException {
        List<Testcase> testcases = repository.findByExercise_Id(exercise.getId());
        List<TestcaseDetailDto> testcaseDetails = testcases.stream()
                .map(tc -> {
                    Optional<TestcaseIODto> testcaseIO = storageService.getTestcase(exercise, tc.getId());
                    return testcaseIO.map(testcaseIODto -> tc.toDetailDto(testcaseIODto.getInput(), testcaseIODto.getOutput()))
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();

        if (testcases.size() != testcaseDetails.size()) {
            throw new IllegalStateException("Some testcase files are missing");
        }
        return testcaseDetails;
    }

    @Override
    public List<Testcase> getSimpleTestcasesFromExercise(Exercise exercise) {
        return repository.findByExercise_Id(exercise.getId());
    }

    @Override
    public TestcaseDetailDto createTestcase(TestcaseDetailDto testcase, Exercise exercise) {
        var savedTestcase = repository.save(new Testcase(exercise, testcase.getIsVisible(), testcase.getIndex()));

        storageService.createTestcase(exercise,
                savedTestcase.toDetailDto(testcase.getInput(), testcase.getOutput()));

        return testcase;
    }

    @Override
    public Testcase updateTestcaseIndex(Testcase testcase, int index) {
        testcase.setIndex(index);
        return repository.save(testcase);
    }

    @Override
    public TestcaseDetailDto updateTestcase(Exercise exercise, TestcaseDetailDto testcase) {
        repository.updateIsVisibleAndIndexById(testcase.getIsVisible(), testcase.getIndex(), testcase.getId());
        log.info(testcase.getIsVisible().toString());
        storageService.updateTestcase(exercise, testcase);
        return testcase;
    }

    @Override
    @Transactional
    public void deleteTestcase(Exercise exercise, UUID testcaseId) {
        submissionResultService.deleteSubmissionResultsByTestcaseId(testcaseId);
        storageService.deleteTestcase(exercise, testcaseId);
        repository.deleteById(testcaseId);
    }
}
