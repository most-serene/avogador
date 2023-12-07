package eu.mostserene.avogador.exerciseservice.exercises;

import eu.mostserene.avogador.exerciseservice.antiplagiarism.AntiPlagiarismService;
import eu.mostserene.avogador.exerciseservice.antiplagiarism.PlagiarismReport;
import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.security.restapicontrol.EnablePublicRestAPI;
import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.strox.Strox;
import eu.mostserene.avogador.exerciseservice.strox.StroxCellType;
import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import eu.mostserene.avogador.exerciseservice.submissions.SubmissionService;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import eu.mostserene.avogador.exerciseservice.trials.TrialService;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialService;
import eu.mostserene.avogador.exerciseservice.utils.BadRequestException;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
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
    private Exercise getExerciseById(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId) {
        Exercise exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(NotFoundException::new);

        CourseRole courseRole = userCourseService.getUserCourseRole(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseRole.getClearance() < CourseRole.STUDENT.getClearance()) {
            throw new ForbiddenException(user);
        }

        if (courseRole.getClearance().equals(CourseRole.STUDENT.getClearance()) &&
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
        if (exercise.getStatement().length() > 10000){
            throw new BadRequestException("Exercise statement is over 10000 characters");
        }
        
        Trial trial = trialService.getTrialById(exercise.getTrialId())
                .orElseThrow(() -> new NotFoundException("Trial " + exercise.getTrialId() + " not found"));

        CourseRole courseRole = userCourseService.getUserCourseRole(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));


        if (courseRole.getClearance() < CourseRole.COLLABORATOR.getClearance()) {
            throw new ForbiddenException(user);
        }

        return exerciseService.createExercise(exercise, trial);
    }

    /**
     * creates the template of an exercise
     */
    @PostMapping("/{exerciseId}/template")
    private void createExerciseTemplate(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId, @RequestBody Strox strox) {
        Exercise exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(NotFoundException::new);

        Trial trial = trialService.getTrialById(exercise.getTrial().getId())
                .orElseThrow(() -> new NotFoundException("Trial " + exercise.getTrial().getId() + " not found"));

        CourseRole courseRole = userCourseService.getUserCourseRole(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseRole.getClearance() < CourseRole.COLLABORATOR.getClearance()) {
            throw new ForbiddenException(user);
        }

        storageService.createExerciseTemplate(exercise, strox);
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

        if (courseRole.getClearance() < CourseRole.COLLABORATOR.getClearance()) {
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
     * @param user       the requesting user
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

        if (courseRole.getClearance() < CourseRole.COLLABORATOR.getClearance()) {
            throw new ForbiddenException(user);
        }

        //TODO: call file system
        exerciseService.deleteExercise(exercise);
    }

    /**
     * Gets the exercises belonging a trial
     * @param user the requesting user
     * @param trialId the id of the trial to which the exercises belong
     * @throws NotFoundException if the trial doesn't exist
     * @throws ForbiddenException if the user has a clearance lower than a student
     * @return the list of exercises of the trial
     * */
    @GetMapping("/trials/{trialId}")
    private List<ExerciseDto> getExercisesFromTrial(@RequestHeader(name = "User") UserDto user, @PathVariable UUID trialId) {
        var trial = trialService.getTrialById(trialId)
                .orElseThrow(() -> new NotFoundException(trialId.toString()));

        var courseRole = userCourseService.getUserCourseRole(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseRole.getClearance() < CourseRole.STUDENT.getClearance()){
            throw new ForbiddenException(user);
        }

        return exerciseService.getExercisesFromTrial(trial, courseRole.hasCollaboratorClearance())
                .stream().map(Exercise::toDto).toList();
    }


    @GetMapping("/{exerciseId}/template")
    private Strox getExerciseTemplate(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId, @RequestParam(defaultValue = "false") boolean merged){
        var exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(() -> new NotFoundException(exerciseId.toString()));

        var courseRole = userCourseService.getUserCourseRole(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseRole.getClearance() < CourseRole.STUDENT.getClearance()){
            throw new ForbiddenException(user);
        }

        Optional<Submission> submission = Optional.empty();
        Strox template;
        if(merged){
            submission = submissionService.getLatestSubmissionFromExerciseAndUserId(exercise, user.getId());
        }

        if (!merged || submission.isEmpty()){
            template = storageService.getExerciseTemplate(exercise)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        }
        else {
            template = storageService.getMergedSubmission(submission.get())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        }

        template.setCells(
                template.getCells()
                        .stream()
                        .filter(cell -> !cell.getType().equals(StroxCellType.HIDDEN) || courseRole.getClearance() >= CourseRole.COLLABORATOR.getClearance())
                        .toList()
        );

        return template;
    }

    @GetMapping("/{exerciseId}/similarity-report")
    private PlagiarismReport getSimilarityReport(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId) {
        var exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(() -> new NotFoundException(exerciseId.toString()));

        var courseRole = userCourseService.getUserCourseRole(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (!user.getIsSuperuser() && !courseRole.hasCollaboratorClearance()){
            throw new ForbiddenException(user);
        }

        return antiPlagiarismService.getSimilarityReport(exercise)
                .orElseThrow(() -> new NotFoundException("Exercise " + exercise.getId() + " Similarity report not found"));
    }

}
