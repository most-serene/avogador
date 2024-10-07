package eu.mostserene.avogador.exerciseservice.exercises;

import eu.mostserene.avogador.exerciseservice.abstractexercises.AbstractExercise;
import eu.mostserene.avogador.exerciseservice.abstractexercises.AbstractExerciseDto;
import eu.mostserene.avogador.exerciseservice.antiplagiarism.AntiPlagiarismService;
import eu.mostserene.avogador.exerciseservice.courses.CourseDetailDto;
import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import eu.mostserene.avogador.exerciseservice.submissions.SubmissionService;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import eu.mostserene.avogador.exerciseservice.trials.TrialService;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialService;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @Autowired
    private StorageService storageService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private AntiPlagiarismService antiPlagiarismService;

    /**
     * Returns the exercise given the exercise ID
     *
     * @param user       the requesting user
     * @param exerciseId the id of the exercise
     * @return the exercise
     */
    @GetMapping("/{exerciseId}")
    private AbstractExercise getExerciseById(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId) {
        AbstractExercise exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(NotFoundException::new);

        CourseRole courseRole = userCourseService.getCourseMember(exercise.getTrial().getCourseId(), user)
                .orElseThrow(() -> new ForbiddenException(user)).getRole();

        if (!exercise.getIsVisible() && courseRole.getClearance() == CourseRole.STUDENT.getClearance() && !user.getIsSuperuser()) {
            throw new ForbiddenException(user);
        }

        return exercise;
    }

    /**
     * Deletes an exercise
     *
     * @param user       the requesting user
     * @param exerciseId the id of the exercise
     */
    @DeleteMapping("/{exerciseId}")
    private void deleteExercise(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId) {
        AbstractExercise exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(NotFoundException::new);

        Trial trial = trialService.getTrialById(exercise.getTrial().getId())
                .orElseThrow(() -> new NotFoundException("Trial " + exercise.getTrial().getId() + " not found"));

        CourseDetailDto courseDetail = userCourseService.getCourseCollaborator(trial.getCourseId(), user)
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseDetail.getIsArchived()) {
            throw new ResponseStatusException(HttpStatus.GONE, "This course has been archived");
        }

        exerciseService.deleteExercise(exercise);
    }

    /**
     * Gets the exercises belonging a trial
     *
     * @param user    the requesting user
     * @param trialId the id of the trial to which the exercises belong
     * @return the list of exercises of the trial
     * @throws NotFoundException  if the trial doesn't exist
     * @throws ForbiddenException if the user has a clearance lower than a student
     */
    @GetMapping("/trials/{trialId}")
    private List<AbstractExerciseDto> getExercisesFromTrial(@RequestHeader(name = "User") UserDto user, @PathVariable UUID trialId) {
        var trial = trialService.getTrialById(trialId)
                .orElseThrow(() -> new NotFoundException(trialId.toString()));

        var courseRole = userCourseService.getCourseMember(trial.getCourseId(), user)
                .orElseThrow(() -> new ForbiddenException(user)).getRole();


        return exerciseService.getExercisesFromTrial(trial, courseRole.hasCollaboratorClearance())
                .stream()
                .map(AbstractExercise::toDto)
                .toList();
    }

}
