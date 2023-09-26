package eu.mostserene.avogador.exerciseservice.exercises;

import eu.mostserene.avogador.exerciseservice.filesystem.FileSystemService;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import eu.mostserene.avogador.exerciseservice.trials.TrialService;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialService;
import eu.mostserene.avogador.exerciseservice.utils.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExerciseServiceImpl implements ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private FileSystemService fileSystemService;

    @Autowired
    private TrialService trialService;

    @Autowired
    private UserTrialService userTrialService;

    @Override
    public Optional<Exercise> getExercise(UUID exerciseId) {
        return exerciseRepository.findById(exerciseId);
    }

    @Override
    public Exercise createExercise(ExerciseDto exerciseDto, Trial trial) {
        Exercise exercise = new Exercise(trial, exerciseDto.getName(), exerciseDto.getStatement(),
                exerciseDto.getTimeLimit(), exerciseDto.getIsVisible());

        Exercise createdExercise = exerciseRepository.save(exercise);
        fileSystemService.createExercise(createdExercise);
        return createdExercise;
    }

    @Override
    public Exercise updateExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    @Override
    public void deleteExercise(Exercise exercise) {
        exerciseRepository.delete(exercise);
        fileSystemService.deleteExercise(exercise);
    }

    @Override
    public List<Exercise> getExercisesFromTrial(Trial trial, Boolean includeHidden) {
        if (!includeHidden){
            return exerciseRepository.findByTrial_Id(trial.getId());
        }
        return exerciseRepository.findByTrial_IdAndIsVisibleTrue(trial.getId());
    }
}
