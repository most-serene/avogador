package eu.mostserene.avogador.exerciseservice.trials;


import eu.mostserene.avogador.exerciseservice.abstractexercises.ExerciseType;
import eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises.CodingExercise;
import eu.mostserene.avogador.exerciseservice.amqp.Sender;
import eu.mostserene.avogador.exerciseservice.antiplagiarism.AntiPlagiarismService;
import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.CourseService;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import eu.mostserene.avogador.exerciseservice.utils.WebSocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/public/trials")
@Slf4j
public class TrialController {
    @Autowired
    private TrialService trialService;
    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private AntiPlagiarismService antiPlagiarismService;

    @Autowired
    private Sender sender;

    @GetMapping("/{trialId}")
    private Trial getTrialById(@RequestHeader(name = "User") UserDto user, @PathVariable UUID trialId) {
        var trial = trialService.getTrialById(trialId).
                orElseThrow(NotFoundException::new);
        var courseRole = userCourseService.getUserCourseRoleDetail(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseRole.getRole().getClearance() < CourseRole.STUDENT.getClearance() && !user.getIsSuperuser()) {
            throw new ForbiddenException(user);
        }

        return trial;
    }

    /**
     * gets all trials from the specified courseID
     *
     * @param user     the requesting user
     * @param courseId the course to which the trials belong
     * @return the list of trials belonging to the course
     * @throws ForbiddenException if the user has a clearance lower than STUDENT
     */
    @GetMapping("/courses/{courseId}")
    private List<Trial> getTrialsFromCourse(@RequestHeader(name = "User") UserDto user, @PathVariable UUID courseId) {
        var userRole = userCourseService.getUserCourseRoleDetail(courseId, user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (!user.getIsSuperuser() && userRole.getRole().getClearance() < CourseRole.STUDENT.getClearance()) {
            throw new ForbiddenException(user);
        }

        if (userRole.getRole().getClearance() == CourseRole.STUDENT.getClearance()) {
            return trialService.getTrialsByCourseId(courseId, false);
        }

        return trialService.getTrialsByCourseId(courseId, true);
    }

    @PutMapping("/{trialId}/similarity")
    private void generateSimilarityReport(@RequestHeader(name = "User") UserDto user, @PathVariable UUID trialId) {
        var trial = trialService.getTrialById(trialId)
                .orElseThrow(() -> new NotFoundException(trialId.toString()));
        var courseRole = userCourseService.getUserCourseRoleDetail(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        var exercises = exerciseService.getExercisesFromTrial(trial, true);

        if (!user.getIsSuperuser() && !courseRole.getRole().hasCollaboratorClearance()) {
            throw new ForbiddenException(user);
        }

        if (courseRole.getIsArchived()) {
            throw new ResponseStatusException(HttpStatus.GONE, "The course is archived");
        }

        CompletableFuture.allOf(
                        exercises.stream()
                                .filter(exercise -> exercise.getExerciseType().equals(ExerciseType.CODING))
                                .map(exercise -> (CodingExercise) exercise)
                                .map(exercise -> CompletableFuture.runAsync(() ->
                                        antiPlagiarismService.executeSimilarityTool(exercise)
                                ))
                                .toList()
                                .toArray(new CompletableFuture[0]))
                .thenAcceptAsync(v -> userCourseService.getCourseCollaborators(trial.getCourseId())
                        .forEach(userCourseDto -> sender.send("users", "users.notify.socket",
                                new WebSocketMessage("/users/" + userCourseDto.getUserId() + "/trials/" + trial.getId() + "/similarity-report",
                                        "Similarity report for " + trial.getName() + " ready"))
                        ));
    }

    /**
     * deletes the specified trial
     *
     * @param user    the requesting user
     * @param trialId the trial to be deleted
     * @throws NotFoundException  if the trial does not exist
     * @throws ForbiddenException if the user has a clearance lower than COLLABORATOR
     */
    @DeleteMapping("/{trialId}")
    private void deleteTrial(@RequestHeader(name = "User") UserDto user, @PathVariable UUID trialId) {
        var trial = trialService.getTrialById(trialId)
                .orElseThrow(() -> new NotFoundException(trialId.toString()));

        var courseDetail = userCourseService.getUserCourseRoleDetail(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseDetail.getRole().getClearance() < CourseRole.COLLABORATOR.getClearance() && !user.getIsSuperuser()) {
            throw new ForbiddenException(user);
        }

        if (courseDetail.getIsArchived()) {
            throw new ResponseStatusException(HttpStatus.GONE, "The course is archived");
        }

        trialService.deleteTrial(trial);
    }
}
