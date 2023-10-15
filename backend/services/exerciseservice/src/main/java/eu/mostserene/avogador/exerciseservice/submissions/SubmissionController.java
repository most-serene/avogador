package eu.mostserene.avogador.exerciseservice.submissions;

import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.strox.StroxException;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialService;
import eu.mostserene.avogador.exerciseservice.utils.BadRequestException;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

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
    private UserTrialService userTrialService;

    @GetMapping("/{submissionId}")
    private SubmissionDto getSubmissionById(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId, @PathVariable UUID submissionId) {
        Exercise exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(NotFoundException::new);

        CourseRole courseRole = userCourseService.getUserCourseRole(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        Submission submission = submissionService.getSubmission(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission " + submissionId.toString() + " not found"));

        if (!user.getIsSuperuser() && courseRole.getClearance() < CourseRole.COLLABORATOR.getClearance() && !user.getId().equals(submission.getUserId())) {
            throw new ForbiddenException(user);
        }

        return submissionService.exportToDto(submission);
    }

    @PostMapping("")
    private SubmissionDto createSubmission(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId, @RequestBody SubmissionDto submissionDto) {
        Exercise exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(NotFoundException::new);

        CourseRole courseRole = userCourseService.getUserCourseRole(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        boolean isUserPrivileged = user.getIsSuperuser() || courseRole.getClearance() >= CourseRole.COLLABORATOR.getClearance();

        if (!exercise.getId().equals(submissionDto.getExerciseId())) {
            throw new BadRequestException("Exercise id not matching");
        }

        if ((!user.getIsSuperuser() && courseRole.getClearance() < CourseRole.STUDENT.getClearance())
                || (!user.getId().equals(submissionDto.getUserId()) || !exercise.getIsVisible() || !exercise.getTrial().getIsVisible())
        ) {
            throw new ForbiddenException(user);
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

        try {
            Submission submission = submissionService.createSubmission(exercise, submissionDto);
            return new SubmissionDto(submission.getId(), submission.getExercise().getId(), submission.getUserId(), submission.getTimestamp());
        } catch (StroxException stroxException) {
            throw new BadRequestException(stroxException.getMessage());
        }
    }
}
