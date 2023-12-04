package eu.mostserene.avogador.exerciseservice.submissionresults;

import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService;
import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import eu.mostserene.avogador.exerciseservice.submissions.SubmissionService;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseService;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrial;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialService;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/exercises/{exerciseId}")
public class SubmissionResultController {
    @Autowired
    private SubmissionResultService submissionResultService;
    @Autowired
    private ExerciseService exerciseService;
    @Autowired
    private UserCourseService userCourseService;
    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private UserTrialService userTrialService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private TestcaseService testcaseService;


    @GetMapping("/users/{userId}/results")
    private Map<UUID, List<SubmissionResultDto>> getResultsFromUser(
            @RequestHeader(name = "User") UserDto user,
            @PathVariable UUID exerciseId,
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "false") boolean latest
    ) {
        var exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(NotFoundException::new);
        var courseRole = userCourseService.getUserCourseRole(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (!user.getIsSuperuser() && courseRole.getClearance() < CourseRole.COLLABORATOR.getClearance() && !user.getId().equals(userId)){
            throw new ForbiddenException(user);
        }

        List<Submission> submissions;
        if (latest){
            submissions = submissionService.getLatestSubmissionFromExerciseAndUserId(exercise, userId)
                    .stream()
                    .toList();
        } else {
            submissions = submissionService.getSubmissionsFromExerciseAndUserId(exercise, userId);
        }

        return submissions.stream()
                .collect(Collectors.toMap(
                        Submission::getId,
                        submission -> submissionResultService.getResultsFromSubmission(submission)
                                .stream()
                                .map(SubmissionResult::toDto)
                                .toList()
                ));
    }

    @GetMapping("/results")
    private List<SubmissionResultSummary> getExerciseResultSummary(
            @RequestHeader(name = "User") UserDto user,
            @PathVariable UUID exerciseId
    ){
        var exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(NotFoundException::new);
        var courseRole = userCourseService.getUserCourseRole(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (!user.getIsSuperuser() && courseRole.getClearance() < CourseRole.COLLABORATOR.getClearance()){
            throw new ForbiddenException(user);
        }

        List<UserTrial> userTrials = userTrialService.getUsersFromTrial(exercise.getTrial());
        return userTrials.stream()
                .map(userTrial -> {
                    var submission = submissionService.getLatestSubmissionFromExerciseAndUserId(exercise, userTrial.getUserId());
                    if (submission.isEmpty()){
                        return new SubmissionResultSummary();
                    }
                    var results = submissionResultService.getResultsFromSubmission(submission.get());
                    return new SubmissionResultSummary(submission.get(), results);
                }).toList();
    }

    @GetMapping("/submissions/{submissionId}/results")
    private List<SubmissionResult> getSubmissionResults(
            @RequestHeader(name = "User") UserDto user,
            @PathVariable UUID exerciseId,
            @PathVariable UUID submissionId
    ){
        var exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(NotFoundException::new);
        var courseRole = userCourseService.getUserCourseRole(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));
        var submission = submissionService.getSubmission(submissionId)
                .orElseThrow(NotFoundException::new);

        if (!user.getIsSuperuser() && courseRole.getClearance() < CourseRole.COLLABORATOR.getClearance() && !user.getId().equals(submission.getUserId())){
            throw new ForbiddenException(user);
        }

        return submissionResultService.getResultsFromSubmission(submission);
    }

    @GetMapping("/submissions/{submissionId}/outputs")
    private Map<String, String> getSubmissionOutputs(
            @RequestHeader(name = "User") UserDto user,
            @PathVariable UUID exerciseId,
            @PathVariable UUID submissionId
    ) {
        var exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(NotFoundException::new);
        var courseRole = userCourseService.getUserCourseRole(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));
        var submission = submissionService.getSubmission(submissionId)
                .orElseThrow(NotFoundException::new);

        if (!user.getIsSuperuser() && courseRole.getClearance() < CourseRole.COLLABORATOR.getClearance() && !user.getId().equals(submission.getUserId())){
            throw new ForbiddenException(user);
        }

        Map<String, String> outputs = storageService.getSubmissionStrox(submission)
                .orElseThrow(NotFoundException::new)
                .getOutputs();

        boolean canSeeHidden = user.getIsSuperuser() || courseRole.hasCollaboratorClearance();

        testcaseService.getSimpleTestcasesFromExercise(exercise)
                .stream()
                .filter(testcase -> !testcase.getIsVisible() && !canSeeHidden)
                .forEach(testcase -> outputs.remove(testcase.getId().toString()));

        return outputs;
    }


    @Data
    private static class SubmissionResultSummary {
        private UUID submissionId;
        private UUID userId;
        private UUID exerciseId;
        private SubmissionStatusSummary status;

        public SubmissionResultSummary(){
            status = SubmissionStatusSummary.MISSING;
        }

        public SubmissionResultSummary(Submission submission, List<SubmissionResult> result) {
            if (result.isEmpty()){
                this.status = SubmissionStatusSummary.MISSING;
            } else if (result.stream().anyMatch(res -> res.getStatus().equals(SubmissionStatus.PENDING))){
                this.status = SubmissionStatusSummary.PENDING;
            } else if (!result.stream().allMatch(res -> res.getStatus().equals(SubmissionStatus.CORRECT))){
                this.status = SubmissionStatusSummary.WRONG;
            } else {
                this.status = SubmissionStatusSummary.CORRECT;
            }
            this.submissionId = submission.getId();
            this.userId = submission.getUserId();
            this.exerciseId = submission.getExercise().getId();
        }
    }

    private enum SubmissionStatusSummary {
        CORRECT,
        WRONG,
        PENDING,
        MISSING
    }

}
