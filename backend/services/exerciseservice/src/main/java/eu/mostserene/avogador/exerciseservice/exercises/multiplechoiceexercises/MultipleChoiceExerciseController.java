package eu.mostserene.avogador.exerciseservice.exercises.multiplechoiceexercises;

import eu.mostserene.avogador.exerciseservice.courses.CourseDetailDto;
import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import eu.mostserene.avogador.exerciseservice.trials.TrialService;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.utils.BadRequestException;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/exercises/multichoice")
@Slf4j
public class MultipleChoiceExerciseController {
    @Autowired
    private MultipleChoiceService multipleChoiceService;
    @Autowired
    private UserCourseService userCourseService;
    @Autowired
    private TrialService trialService;

    @PostMapping("")
    private MultipleChoiceExercise createExercise(@RequestHeader(name = "User") UserDto user, @RequestBody MultipleChoiceExerciseDto exercise) {
        if (exercise.getStatement().length() > 10000) {
            throw new BadRequestException("Exercise statement is over 10000 characters");
        }

        Trial trial = trialService.getTrialById(exercise.getTrialId())
                .orElseThrow(() -> new NotFoundException("Trial " + exercise.getTrialId() + " not found"));

        CourseDetailDto courseDetail = userCourseService.getCourseCollaborator(trial.getCourseId(), user)
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseDetail.getIsArchived()) {
            throw new ResponseStatusException(HttpStatus.GONE, "This course is archived");
        }

        if (exercise.getWrongPoints() > 0) {
            throw new BadRequestException("Wrong points cannot be positive");
        }

        if (exercise.getCorrectPoints() < 0) {
            throw new BadRequestException("Correct points cannot be negative");
        }

        int numCorrectOptions = exercise.getOptions().stream().filter(MultipleChoiceOptionDto::getIsCorrect).toList().size();
        if (numCorrectOptions == 0) {
            throw new BadRequestException("No correct option set");
        }
        if (numCorrectOptions > 1 && !exercise.getHasMultipleAnswers()) {
            throw new BadRequestException("More than one correct option set");
        }

        return multipleChoiceService.createMultipleChoiceExercise(exercise, trial);
    }

    @PutMapping("/{exerciseId}")
    private MultipleChoiceExercise updateExercise(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId, @RequestBody MultipleChoiceExerciseDto exercise) {
        Trial trial = trialService.getTrialById(exercise.getTrialId())
                .orElseThrow(() -> new NotFoundException("Trial " + exercise.getTrialId() + " not found"));

        CourseDetailDto courseDetail = userCourseService.getCourseCollaborator(trial.getCourseId(), user)
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseDetail.getIsArchived()) {
            throw new ResponseStatusException(HttpStatus.GONE, "This course is archived");
        }

        MultipleChoiceExercise existingExercise = multipleChoiceService.getMultipleChoiceExercise(exerciseId)
                .orElseThrow(() -> new NotFoundException("Exercise " + exerciseId + " not found"));

        if (!existingExercise.getId().equals(exercise.getId())) {
            throw new BadRequestException("Exercise Id mismatch");
        }

        if (!existingExercise.getTrial().getId().equals(exercise.getTrialId())) {
            throw new BadRequestException("Trial Id mismatch");
        }

        existingExercise.setName(exercise.getName());
        existingExercise.setStatement(exercise.getStatement());
        existingExercise.setIsVisible(exercise.getIsVisible());
        existingExercise.setHasMultipleAnswers(exercise.getHasMultipleAnswers());
        existingExercise.setCorrectPoints(exercise.getCorrectPoints());
        existingExercise.setWrongPoints(exercise.getWrongPoints());
        existingExercise.setStrictMode(exercise.getStrictMode());
        existingExercise.setHasShuffling(exercise.getHasShuffling());

        int numCorrectOptions = exercise.getOptions().stream().filter(MultipleChoiceOptionDto::getIsCorrect).toList().size();
        if (numCorrectOptions == 0) {
            throw new BadRequestException("No correct option set");
        }
        if (numCorrectOptions > 1 && !exercise.getHasMultipleAnswers()) {
            throw new BadRequestException("More than one correct option set");
        }

        Map<UUID, MultipleChoiceOption> oldOptions = multipleChoiceService.getExerciseOptions(exerciseId)
                .stream()
                .collect(Collectors.toMap(MultipleChoiceOption::getId, option -> option));

        AtomicInteger index = new AtomicInteger(0);
        exercise.getOptions().forEach(option -> {
            if (option.getId() != null && !oldOptions.containsKey(option.getId())) {
                throw new BadRequestException("Non-existing option with id " + option.getId());
            }
            MultipleChoiceOption updatedOption = oldOptions.get(option.getId());
            updatedOption.setLabel(option.getLabel());
            updatedOption.setIsCorrect(option.getIsCorrect());
            multipleChoiceService.saveMultipleChoiceOption(updatedOption, index.getAndIncrement());
        });

        oldOptions.keySet().stream()
                .filter(optionId -> exercise.getOptions().stream().noneMatch(newOption -> newOption.getId() == optionId))
                .forEach(optionId -> multipleChoiceService.deleteMultipleChoiceOption(optionId));

        return multipleChoiceService.updateMultipleChoiceExercise(existingExercise);
    }

    @GetMapping("/{exerciseId}/options")
    private List<MultipleChoiceOption> getExerciseOptions(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId) {
        MultipleChoiceExercise exercise = multipleChoiceService.getMultipleChoiceExercise(exerciseId)
                .orElseThrow(() -> new NotFoundException(exerciseId.toString()));
        CourseRole courseRole = userCourseService.getCourseMember(exercise.getTrial().getCourseId(), user)
                .orElseThrow(() -> new ForbiddenException(user))
                .getRole();

        if (!exercise.getIsVisible() && !user.getIsSuperuser() && !courseRole.hasCollaboratorClearance()) {
            throw new ForbiddenException(user);
        }

        List<MultipleChoiceOption> options = multipleChoiceService.getExerciseOptions(exerciseId);

        if (exercise.getHasShuffling()) {
            Collections.shuffle(options, new Random(user.getId().getLeastSignificantBits() + exercise.getId().getLeastSignificantBits()));
        }

        return options;
    }
}
