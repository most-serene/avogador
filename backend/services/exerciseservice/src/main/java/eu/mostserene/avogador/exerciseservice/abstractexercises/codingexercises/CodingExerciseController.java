package eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises;

import eu.mostserene.avogador.exerciseservice.abstractexercises.AbstractExerciseDto;
import eu.mostserene.avogador.exerciseservice.antiplagiarism.AntiPlagiarismService;
import eu.mostserene.avogador.exerciseservice.antiplagiarism.PlagiarismReport;
import eu.mostserene.avogador.exerciseservice.courses.CourseDetailDto;
import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import eu.mostserene.avogador.exerciseservice.strox.Strox;
import eu.mostserene.avogador.exerciseservice.strox.StroxCellType;
import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import eu.mostserene.avogador.exerciseservice.submissions.SubmissionService;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseDetailDto;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseService;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/public/exercises/coding")
@Slf4j
public class CodingExerciseController {

    @Autowired
    private TrialService trialService;

    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private CodingExerciseService codingExerciseService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private AntiPlagiarismService antiPlagiarismService;

    @Autowired
    private TestcaseService testcaseService;

    /**
     * Creates an exercise in a given trial
     *
     * @param user     the requesting user
     * @param exercise the DTO of the exercise to create
     * @return the created exercise
     */
    @PostMapping("")
    private CodingExercise createExercise(@RequestHeader(name = "User") UserDto user, @RequestBody CodingExerciseDto exercise) {
        if (exercise.getStatement().length() > 10000) {
            throw new BadRequestException("Exercise statement is over 10000 characters");
        }

        Trial trial = trialService.getTrialById(exercise.getTrialId())
                .orElseThrow(() -> new NotFoundException("Trial " + exercise.getTrialId() + " not found"));

        CourseDetailDto courseDetail = userCourseService.getUserCourseRoleDetail(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseDetail.getRole().getClearance() < CourseRole.COLLABORATOR.getClearance() && !user.getIsSuperuser()) {
            throw new ForbiddenException(user);
        }

        if (courseDetail.getIsArchived()) {
            throw new ResponseStatusException(HttpStatus.GONE, "This course is archived");
        }

        return codingExerciseService.createCodingExercise(exercise, trial);
    }

    /**
     * creates the template of an exercise
     */
    @PostMapping("/{exerciseId}/template")
    private void createExerciseTemplate(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId, @RequestBody Strox strox) {
        CodingExercise exercise = codingExerciseService.getCodingExercise(exerciseId)
                .orElseThrow(NotFoundException::new);

        Trial trial = trialService.getTrialById(exercise.getTrial().getId())
                .orElseThrow(() -> new NotFoundException("Trial " + exercise.getTrial().getId() + " not found"));

        CourseDetailDto courseDetail = userCourseService.getUserCourseRoleDetail(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseDetail.getRole().getClearance() < CourseRole.COLLABORATOR.getClearance() && !user.getIsSuperuser()) {
            throw new ForbiddenException(user);
        }

        if (courseDetail.getIsArchived()) {
            throw new ResponseStatusException(HttpStatus.GONE, "This course is archived");
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
    private AbstractExerciseDto updateExercise(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId, @RequestBody CodingExerciseDto exercise) {
        Trial trial = trialService.getTrialById(exercise.getTrialId())
                .orElseThrow(() -> new NotFoundException("Trial " + exercise.getTrialId() + " not found"));

        CourseDetailDto courseDetail = userCourseService.getUserCourseRoleDetail(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseDetail.getRole().getClearance() < CourseRole.COLLABORATOR.getClearance() && !user.getIsSuperuser()) {
            throw new ForbiddenException(user);
        }

        if (courseDetail.getIsArchived()) {
            throw new ResponseStatusException(HttpStatus.GONE, "This course is archived");
        }

        CodingExercise existingExercise = codingExerciseService.getCodingExercise(exerciseId)
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

        return codingExerciseService.updateCodingExercise(existingExercise).toDto();
    }

    @GetMapping("/{exerciseId}/template")
    private Strox getExerciseTemplate(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId, @RequestParam(defaultValue = "false") boolean merged) {
        var exercise = codingExerciseService.getCodingExercise(exerciseId)
                .orElseThrow(() -> new NotFoundException(exerciseId.toString()));

        var courseRole = userCourseService.getUserCourseRoleDetail(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user)).getRole();

        if (courseRole.getClearance() < CourseRole.STUDENT.getClearance() && !user.getIsSuperuser()) {
            throw new ForbiddenException(user);
        }

        Optional<Submission> submission = Optional.empty();
        Strox template;
        if (merged) {
            submission = submissionService.getLatestSubmissionFromExerciseAndUserId(exercise, user.getId());
        }

        if (!merged || submission.isEmpty()) {
            template = storageService.getExerciseTemplate(exercise)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        } else {
            template = storageService.getMergedSubmission(submission.get())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        }

        template.setCells(
                template.getCells()
                        .stream()
                        .filter(cell -> !cell.getType().equals(StroxCellType.HIDDEN) || courseRole.getClearance() >= CourseRole.COLLABORATOR.getClearance() || user.getIsSuperuser())
                        .toList()
        );

        return template;
    }

    @GetMapping("/{exerciseId}/similarity-report-presence")
    private boolean getSimilarityReportPresence(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId) {
        var exercise = codingExerciseService.getCodingExercise(exerciseId)
                .orElseThrow(() -> new NotFoundException(exerciseId.toString()));

        var courseRole = userCourseService.getUserCourseRoleDetail(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user)).getRole();

        if (!user.getIsSuperuser() && !courseRole.hasCollaboratorClearance()) {
            throw new ForbiddenException(user);
        }

        return antiPlagiarismService.getSimilarityReport(exercise).isPresent();
    }

    @GetMapping("/{exerciseId}/similarity-report")
    private PlagiarismReport getSimilarityReport(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId) {
        var exercise = codingExerciseService.getCodingExercise(exerciseId)
                .orElseThrow(() -> new NotFoundException(exerciseId.toString()));

        var courseRole = userCourseService.getUserCourseRoleDetail(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user)).getRole();

        if (!user.getIsSuperuser() && !courseRole.hasCollaboratorClearance()) {
            throw new ForbiddenException(user);
        }

        return antiPlagiarismService.retrieveSimilarityReportFile(exercise)
                .orElseThrow(() -> new NotFoundException("Exercise " + exercise.getId() + " Similarity report not found"));
    }

    @GetMapping("/{exerciseId}/export")
    private CodingExerciseDump exeportExercise(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId) {
        CodingExercise exercise = codingExerciseService.getCodingExercise(exerciseId)
                .orElseThrow(NotFoundException::new);

        var courseRole = userCourseService.getUserCourseRoleDetail(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user)).getRole();

        if (!user.getIsSuperuser() && !courseRole.hasCollaboratorClearance()) {
            throw new ForbiddenException(user);
        }

        Strox template = storageService.getExerciseTemplate(exercise)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));

        List<TestcaseDetailDto> testcases = testcaseService.getTestcasesFromExercise(exercise);

        return new CodingExerciseDump(exercise, template, testcases);
    }
}
