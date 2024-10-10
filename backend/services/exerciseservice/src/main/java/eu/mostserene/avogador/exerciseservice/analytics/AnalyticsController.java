package eu.mostserene.avogador.exerciseservice.analytics;

import eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises.CodingExerciseService;
import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionStatus;
import eu.mostserene.avogador.exerciseservice.trials.TrialService;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/public/analytics")
@Slf4j
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private TrialService trialService;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private CodingExerciseService codingExerciseService;

    @GetMapping("/users/{userId}/courses/{courseId}/progress")
    private List<StudentTrialStatus> getStudentProgress(@RequestHeader(name = "User") UserDto user,
                                                        @PathVariable UUID userId,
                                                        @PathVariable UUID courseId
    ) {
        CourseRole courseRole = userCourseService.getCourseMember(courseId, user)
                .orElseThrow(NotFoundException::new)
                .getRole();

        if (!user.getIsSuperuser() && !courseRole.hasCollaboratorClearance() && !user.getId().equals(userId)) {
            throw new ForbiddenException(user);
        }

        return analyticsService.getStudentProgress(userId, courseId)
                .values().stream().toList();
    }

    @GetMapping("/trials/{trialId}/exercises/{exerciseId}/results")
    private Map<SubmissionStatus, Long> getExerciseResults(@RequestHeader(name = "User") UserDto user,
                                                           @PathVariable UUID trialId,
                                                           @PathVariable UUID exerciseId
    ) {
        var trial = trialService.getTrialById(trialId)
                .orElseThrow(() -> new NotFoundException("Trial with id: " + trialId));
        userCourseService.getCourseCollaborator(trial.getCourseId(), user)
                .orElseThrow(NotFoundException::new);
        var exercise = codingExerciseService.getCodingExercise(exerciseId)
                .orElseThrow(() -> new NotFoundException("Exercise with id: " + trialId));

        if (exercise.getTrial().getId() != trialId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercise does not belong to the specified Trial");
        }

        return analyticsService.getExerciseResults(exercise);
    }

    @GetMapping("/courses/{courseId}/submissions-trend")
    private List<Date> getCourseSubmissionTrend(@RequestHeader(name = "User") UserDto user,
                                                @PathVariable UUID courseId) {
        userCourseService.getCourseCollaborator(courseId, user)
                .orElseThrow(NotFoundException::new);

        return analyticsService.getSubmissionsTrend(courseId);
    }

}
