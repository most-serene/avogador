package eu.mostserene.avogador.exerciseservice.exercises;

import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionResultService;
import eu.mostserene.avogador.exerciseservice.submissions.SubmissionService;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseService;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class ExerciseServiceImpl implements ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private TestcaseService testcaseService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private SubmissionResultService submissionResultService;

    @Override
    public Optional<Exercise> getExercise(UUID exerciseId) {
        return exerciseRepository.findById(exerciseId);
    }

    @Override
    public Exercise createExercise(ExerciseDto exerciseDto, Trial trial) {
        Exercise exercise = new Exercise(trial, exerciseDto.getName(), exerciseDto.getStatement(),
                exerciseDto.getTimeLimit(), exerciseDto.getIsVisible());

        Exercise createdExercise = exerciseRepository.save(exercise);
        storageService.createExercise(createdExercise);
        return createdExercise;
    }

    @Override
    public Exercise updateExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    @Override
    public void deleteExercise(Exercise exercise) {
        testcaseService.deleteTestcases(exercise);
        submissionService.deleteSubmissions(exercise);

        exerciseRepository.delete(exercise);
        storageService.deleteExercise(exercise);
    }

    /**
     * Warning: this method will not remove the exercise storage files in order to prevent overhead while
     * deleting a trial
     *
     * @param trial
     */
    @Override
    public void deleteExercisesByTrial(Trial trial) {
        exerciseRepository.findByTrial_Id(trial.getId())
                .forEach(exercise -> {
                    testcaseService.deleteTestcases(exercise);
                    submissionService.deleteSubmissions(exercise);

                    exerciseRepository.delete(exercise);
                });
    }

    @Override
    public List<Exercise> getExercisesFromTrial(Trial trial, Boolean includeHidden) {
        if (includeHidden) {
            return exerciseRepository.findByTrial_Id(trial.getId());
        }
        return exerciseRepository.findByTrial_IdAndIsVisibleTrue(trial.getId());
    }
}
