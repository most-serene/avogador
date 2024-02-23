package eu.mostserene.avogador.exerciseservice.submissions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.exerciseservice.amqp.Sender;
import eu.mostserene.avogador.exerciseservice.courses.CourseDetailDto;
import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.CourseService;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import eu.mostserene.avogador.exerciseservice.strox.StroxException;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionResult;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionResultService;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionStatus;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseService;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialService;
import eu.mostserene.avogador.exerciseservice.utils.BadRequestException;
import eu.mostserene.avogador.exerciseservice.utils.LoggerColors;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import eu.mostserene.avogador.exerciseservice.utils.WebSocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/public/exercises/{exerciseId}/submissions")
@Slf4j
public class SubmissionController {

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private SubmissionResultService submissionResultService;

    @Autowired
    private TestcaseService testcaseService;

    @Autowired
    private UserTrialService userTrialService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private Sender sender;

    @GetMapping("/{submissionId}")
    private SubmissionDto getSubmissionById(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId, @PathVariable UUID submissionId) {
        Exercise exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(NotFoundException::new);

        CourseRole courseRole = userCourseService.getUserCourseRoleDetail(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user)).getRole();

        Submission submission = submissionService.getSubmission(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission " + submissionId.toString() + " not found"));

        if (!user.getIsSuperuser() && courseRole.getClearance() < CourseRole.COLLABORATOR.getClearance() && !user.getId().equals(submission.getUserId())) {
            throw new ForbiddenException(user);
        }

        return submissionService.exportToDto(submission);
    }

    @GetMapping("/{submissionId}/download")
    private ResponseEntity<Resource> downloadSubmissionById(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId, @PathVariable UUID submissionId) {
        Exercise exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(NotFoundException::new);

        CourseRole courseRole = userCourseService.getUserCourseRoleDetail(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user)).getRole();

        Submission submission = submissionService.getSubmission(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission " + submissionId.toString() + " not found"));

        if (!user.getIsSuperuser() && !courseRole.hasCollaboratorClearance()) {
            throw new ForbiddenException(user);
        }

        Resource submissionSource = storageService.getSubmissionSource(submission)
                .orElseThrow(() -> new NotFoundException("Submission - " + submissionId + ": sourcecode not found"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"submission.tar.gz\"")
                .body(submissionSource);
    }

    @GetMapping("/users/{userId}")
    private List<SubmissionDto> getUserSubmissions(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId, @PathVariable UUID userId) {
        Exercise exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(NotFoundException::new);

        CourseRole courseRole = userCourseService.getUserCourseRoleDetail(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user)).getRole();

        if (!user.getIsSuperuser() && courseRole.getClearance() < CourseRole.COLLABORATOR.getClearance() && !user.getId().equals(userId)) {
            throw new ForbiddenException(user);
        }

        List<Submission> submissions = submissionService.getSubmissionsFromExerciseAndUserId(exercise, userId);

        return submissions.stream()
                .map(submission -> new SubmissionDto(submission.getId(),
                                submission.getExercise().getId(),
                                userId,
                                submission.getTimestamp(),
                                storageService.getSubmissionStrox(submission)
                                        .orElseThrow(() -> new NotFoundException(submission.getId() + " Strox not saved"))
                                        .getCells()
                        )
                )
                .toList();
    }

    @PostMapping("")
    private SubmissionDto createSubmission(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId, @RequestBody SubmissionDto submissionDto) {
        Exercise exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(NotFoundException::new);

        CourseDetailDto course = userCourseService.getUserCourseRoleDetail(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (course.getIsArchived()) {
            throw new ResponseStatusException(HttpStatus.GONE, "The course has been archived");
        }

        CourseRole courseRole = course.getRole();

        if (!exercise.getId().equals(submissionDto.getExerciseId())) {
            throw new BadRequestException("Exercise id not matching");
        }

        boolean isUserPrivileged = user.getIsSuperuser() || courseRole.getClearance() >= CourseRole.COLLABORATOR.getClearance();
        boolean isExternal = !user.getIsSuperuser() && courseRole.getClearance() < CourseRole.STUDENT.getClearance();
        boolean hasUserIdMismatch = !user.getId().equals(submissionDto.getUserId());
        boolean isExerciseHidden = !exercise.getIsVisible();
        boolean isTrialHidden = !exercise.getTrial().getIsVisible();

        if (isExternal) {
            throw new ForbiddenException(user, "External User");
        }
        if (hasUserIdMismatch) {
            throw new ForbiddenException(user, "UserId mismatch");
        }
        if (!isUserPrivileged && (isExerciseHidden || isTrialHidden)) {
            throw new ForbiddenException(user, "Hidden Exercise or Trial");
        }

        if (!isUserPrivileged) {
            Timestamp deadline = Timestamp.from(userTrialService.getUserTrial(exercise.getTrial(), user)
                    .orElseThrow(() -> new ForbiddenException(user))
                    .getDeadline()
                    .toInstant());

            if (Timestamp.from(Instant.now()).after(deadline)) {
                throw new BadRequestException("The deadline has passed");
            }
        }

        Optional<Submission> lastSubmission = submissionService.getLatestSubmissionFromExerciseAndUserId(exercise, user.getId());

        boolean isAlreadyPending = lastSubmission.isPresent() && submissionResultService.getResultsFromSubmission(lastSubmission.get())
                .stream().anyMatch(submissionResult -> submissionResult.getStatus().equals(SubmissionStatus.PENDING));

        if (isAlreadyPending) {
            throw new ForbiddenException(user, "Keep calm - Too many submissions");
        }

        Submission submission = submissionService.createSubmission(exercise, submissionDto);
        ObjectMapper mapper = new ObjectMapper();

        testcaseService.getSimpleTestcasesFromExercise(submission.getExercise())
                .stream().map(testcase -> submissionResultService.saveSubmissionResult(
                        new SubmissionResult(submission, testcase, SubmissionStatus.PENDING)
                ))
                .forEach(submissionResult -> new Timer().schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    sender.send("users", "users.notify.socket",
                                            new WebSocketMessage("/" + submissionResult.getSubmission().getId() + "/results",
                                                    mapper.writeValueAsString(submissionResult.toDto())
                                            ));
                                } catch (JsonProcessingException e) {
                                    log.error(LoggerColors.error(e.toString()));
                                }
                            }
                        },
                        1000
                ));

        try {
            return new SubmissionDto(submission.getId(), submission.getExercise().getId(), submission.getUserId(), submission.getTimestamp());
        } catch (StroxException stroxException) {
            throw new BadRequestException(stroxException.getMessage());
        }
    }
}
