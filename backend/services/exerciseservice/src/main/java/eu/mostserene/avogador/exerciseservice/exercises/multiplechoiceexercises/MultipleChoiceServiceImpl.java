package eu.mostserene.avogador.exerciseservice.exercises.multiplechoiceexercises;

import eu.mostserene.avogador.exerciseservice.trials.Trial;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
public class MultipleChoiceServiceImpl implements MultipleChoiceService {
    @Autowired
    MultipleChoiceExerciseRepository MCERepository;

    @Autowired
    MultipleChoiceOptionRepository MCORepository;

    @Override
    public Optional<MultipleChoiceExercise> getMultipleChoiceExercise(UUID exerciseId) {
        return MCERepository.findById(exerciseId);
    }

    @Override
    public List<MultipleChoiceOption> getExerciseOptions(UUID exerciseId) {
        return MCORepository.findByExercise_Id(exerciseId).stream().sorted(Comparator.comparing(MultipleChoiceOption::getIndex)).toList();
    }

    @Override
    public MultipleChoiceExercise createMultipleChoiceExercise(MultipleChoiceExerciseDto exerciseDto, Trial trial) {
        MultipleChoiceExercise exercise = new MultipleChoiceExercise(trial, exerciseDto.getName(), exerciseDto.getStatement(),
                exerciseDto.getIsVisible(), exerciseDto.getHasMultipleAnswers(), exerciseDto.getCorrectPoints(),
                exerciseDto.getWrongPoints(), exerciseDto.getStrictMode(), exerciseDto.getHasShuffling());

        AtomicInteger index = new AtomicInteger();
        exerciseDto.getOptions().forEach(option -> {
            saveMultipleChoiceOption(new MultipleChoiceOption(exercise, option.getLabel(), option.getIsCorrect(), 0), index.getAndIncrement());
        });

        return MCERepository.save(exercise);
    }

    @Override
    public MultipleChoiceExercise updateMultipleChoiceExercise(MultipleChoiceExercise exercise) {
        return MCERepository.save(exercise);
    }

    @Override
    public MultipleChoiceOption saveMultipleChoiceOption(MultipleChoiceOption option, int index) {
        option.setIndex(index);
        return MCORepository.save(option);
    }

    @Override
    public void deleteMultipleChoiceOption(UUID optionId) {
        MCORepository.deleteById(optionId);
    }


}
