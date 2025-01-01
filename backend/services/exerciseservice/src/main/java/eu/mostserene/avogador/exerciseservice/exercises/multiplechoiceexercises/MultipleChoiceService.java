package eu.mostserene.avogador.exerciseservice.exercises.multiplechoiceexercises;

import eu.mostserene.avogador.exerciseservice.trials.Trial;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MultipleChoiceService {
    Optional<MultipleChoiceExercise> getMultipleChoiceExercise(UUID exerciseId);

    List<MultipleChoiceOption> getExerciseOptions(UUID exerciseId);

    MultipleChoiceExercise createMultipleChoiceExercise(MultipleChoiceExerciseDto exercise, Trial trial);

    MultipleChoiceExercise updateMultipleChoiceExercise(MultipleChoiceExercise exercise);

    MultipleChoiceOption saveMultipleChoiceOption(MultipleChoiceOption option, int index);

    void deleteMultipleChoiceOption(UUID optionId);
}
