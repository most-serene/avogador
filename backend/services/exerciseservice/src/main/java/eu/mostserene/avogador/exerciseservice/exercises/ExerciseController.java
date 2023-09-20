package eu.mostserene.avogador.exerciseservice.exercises;

import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import eu.mostserene.avogador.exerciseservice.trials.TrialService;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrial;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialService;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/public/exercises")
@Slf4j
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private UserTrialService userTrialService;

    /**
     * Returns the exercise given the exercise ID
     * @param user the requesting user
     * @param exerciseId the id of the exercise
     * @return the exercise
     */
    @GetMapping("/{exerciseId}")
    private Exercise getExerciseById(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId) {
        Exercise exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(NotFoundException::new);

        userTrialService.getUserTrial(exercise.getTrial(), user)
                .orElseThrow(() -> new ForbiddenException(user));

        return exercise;
    }

    /**
     * Creates an exercise in a given trial
     * @param user the requesting user
     * @param exercise the DTO of the exercise to create
     * @return the created exercise
     */
    @PostMapping("")
    private Exercise createExercise(@RequestHeader(name = "User") UserDto user, @RequestBody ExerciseDto exercise) {
        throw new UnsupportedOperationException();
    }

    /**
     * Updates an existing exercise
     * @param user the requesting user
     * @param exerciseId the id of the exercise to update
     * @param exercise the updated exercise
     * @return the saved updated exercise
     */
    @PutMapping("/{exerciseId}")
    private Exercise updateExercise(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId, @RequestBody ExerciseDto exercise) {
        throw new UnsupportedOperationException();
    }

    /**
     * Deletes an exercise
     * @param user the requesting exercise
     * @param exerciseId the id of the exercise
     * @return the deleted exercise
     */
    @DeleteMapping("/{exerciseId}")
    private Exercise deleteExercise(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId) {
        throw new UnsupportedOperationException();
    }
}
