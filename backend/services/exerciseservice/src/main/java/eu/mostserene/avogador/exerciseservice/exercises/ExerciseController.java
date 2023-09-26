package eu.mostserene.avogador.exerciseservice.exercises;

import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import eu.mostserene.avogador.exerciseservice.trials.TrialService;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialService;
import eu.mostserene.avogador.exerciseservice.utils.BadRequestException;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/public/exercises")
@Slf4j
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private UserTrialService userTrialService;

    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private TrialService trialService;

    /**
     * Returns the exercise given the exercise ID
     *
     * @param user       the requesting user
     * @param exerciseId the id of the exercise
     * @return the exercise
     */
    @GetMapping("/{exerciseId}")
    private Exercise getExerciseById(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId) {
        Exercise exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(NotFoundException::new);

        CourseRole courseRole = userCourseService.getUserCourseRole(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (CourseRole.STUDENT.getClearance() > courseRole.getClearance()) {
            throw new ForbiddenException(user);
        }

        if (CourseRole.STUDENT.getClearance().equals(courseRole.getClearance()) &&
                !exercise.getIsVisible()) {
            throw new ForbiddenException(user);
        }

        return exercise;
    }

    /**
     * Creates an exercise in a given trial
     *
     * @param user     the requesting user
     * @param exercise the DTO of the exercise to create
     * @return the created exercise
     */
    @PostMapping("")
    private Exercise createExercise(@RequestHeader(name = "User") UserDto user, @RequestBody ExerciseDto exercise) {
        Trial trial = trialService.getTrialById(exercise.getTrialId())
                .orElseThrow(() -> new NotFoundException("Trial " + exercise.getTrialId() + " not found"));

        CourseRole courseRole = userCourseService.getUserCourseRole(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (CourseRole.COLLABORATOR.getClearance() > courseRole.getClearance()) {
            throw new ForbiddenException(user);
        }

        return exerciseService.createExercise(exercise, trial);
    }

    /**
     * Updates an existing exercise
     *
     * @param user       the requesting user
     * @param exerciseId the id of the exercise to update
     * @param exercise   the updated exercise
     * @return the saved updated exercise
     */
    @PutMapping("/{exerciseId}")
    private Exercise updateExercise(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId, @RequestBody ExerciseDto exercise) {
        Trial trial = trialService.getTrialById(exercise.getTrialId())
                .orElseThrow(() -> new NotFoundException("Trial " + exercise.getTrialId() + " not found"));

        CourseRole courseRole = userCourseService.getUserCourseRole(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (CourseRole.COLLABORATOR.getClearance() > courseRole.getClearance()) {
            throw new ForbiddenException(user);
        }

        Exercise existingExercise = exerciseService.getExercise(exerciseId)
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
        existingExercise.setTimeLimit(exercise.getTimeLimit());

        return exerciseService.updateExercise(existingExercise);
    }

    /**
     * Deletes an exercise
     *
     * @param user       the requesting exercise
     * @param exerciseId the id of the exercise
     */
    @DeleteMapping("/{exerciseId}")
    private void deleteExercise(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId) {
        Exercise exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(NotFoundException::new);

        Trial trial = trialService.getTrialById(exercise.getTrial().getId())
                .orElseThrow(() -> new NotFoundException("Trial " + exercise.getTrial().getId() + " not found"));

        CourseRole courseRole = userCourseService.getUserCourseRole(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (CourseRole.COLLABORATOR.getClearance() > courseRole.getClearance()) {
            throw new ForbiddenException(user);
        }

        exerciseService.deleteExercise(exercise);
    }

    @GetMapping("/trials/{trialId}")
    private List<ExerciseDto> getExercisesFromTrial(@RequestHeader(name = "User") UserDto user, @PathVariable UUID trialId) {
        var trial = trialService.getTrialById(trialId)
                .orElseThrow(() -> new NotFoundException(trialId.toString()));

        var courseRole = userCourseService.getUserCourseRole(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseRole.getClearance() < CourseRole.STUDENT.getClearance()){
            throw new ForbiddenException(user);
        }

        List<Exercise> res;

        if (courseRole.getClearance() < CourseRole.COLLABORATOR.getClearance()){
            res = exerciseService.getExercisesFromTrial(trial, false);
        }
        else {
            res = exerciseService.getExercisesFromTrial(trial, true);
        }

        return res.stream().map(Exercise::toDto).toList();
    }

}
