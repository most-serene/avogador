package eu.mostserene.avogador.exerciseservice.exercises;

import eu.mostserene.avogador.exerciseservice.abstractexercises.AbstractExercise;
import eu.mostserene.avogador.exerciseservice.abstractexercises.AbstractExerciseRepository;
import eu.mostserene.avogador.exerciseservice.abstractexercises.ExerciseType;
import eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises.CodingExercise;
import eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises.CodingExerciseService;
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
    private AbstractExerciseRepository exerciseRepository;

    @Autowired
    private CodingExerciseService codingExerciseService;

    @Override
    public Optional<AbstractExercise> getExercise(UUID exerciseId) {
        return exerciseRepository.findById(exerciseId);
    }

    @Override
    public List<AbstractExercise> getExercisesFromTrial(Trial trial, Boolean includeHidden) {
        if (includeHidden) {
            return exerciseRepository.findByTrial_Id(trial.getId());
        }
        return exerciseRepository.findByTrial_IdAndIsVisibleTrue(trial.getId());
    }

    @Override
    public void deleteExercise(AbstractExercise exercise) {
        if (exercise.getExerciseType().equals(ExerciseType.CODING)) {
            codingExerciseService.deleteCodingExercise((CodingExercise) exercise);
        }
        exerciseRepository.delete(exercise);
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
                .forEach(this::deleteExercise);
    }

}
