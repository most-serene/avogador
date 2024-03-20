package eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises;

import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import eu.mostserene.avogador.exerciseservice.submissions.SubmissionService;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseService;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class CodingExerciseServiceImpl implements CodingExerciseService {

    @Autowired
    private CodingExerciseRepository repository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private TestcaseService testcaseService;

    @Autowired
    private SubmissionService submissionService;

    @Override
    public Optional<CodingExercise> getCodingExercise(UUID exerciseId) {
        return repository.findById(exerciseId);
    }

    @Override
    public CodingExercise createCodingExercise(CodingExerciseDto codingExerciseDto, Trial trial) {
        CodingExercise exercise = new CodingExercise(trial, codingExerciseDto.getName(), codingExerciseDto.getStatement(),
                codingExerciseDto.getIsVisible(), codingExerciseDto.getTimeLimit(), trial.getLanguage());

        CodingExercise createdExercise = repository.save(exercise);
        storageService.createExercise(createdExercise);
        return createdExercise;
    }

    @Override
    public CodingExercise updateCodingExercise(CodingExercise codingExercise) {
        return repository.save(codingExercise);
    }

    @Override
    public void deleteCodingExercise(CodingExercise codingExercise) {
        testcaseService.deleteTestcases(codingExercise);
        submissionService.deleteSubmissions(codingExercise);

        repository.delete(codingExercise);
        storageService.deleteExercise(codingExercise);
    }
}
