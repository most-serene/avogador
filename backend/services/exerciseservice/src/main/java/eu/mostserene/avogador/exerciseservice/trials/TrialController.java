package eu.mostserene.avogador.exerciseservice.trials;


import eu.mostserene.avogador.exerciseservice.antiplagiarism.AntiPlagiarismService;
import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialDetailDto;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/public/trials")
@Slf4j
public class TrialController {
    @Autowired
    private TrialService trialService;
    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private AntiPlagiarismService antiPlagiarismService;

    @GetMapping("/{trialId}")
    private Trial getTrialById(@RequestHeader(name = "User") UserDto user, @PathVariable UUID trialId){
        var trial = trialService.getTrialById(trialId).
                orElseThrow(NotFoundException::new);
        var courseRole = userCourseService.getUserCourseRole(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseRole.getClearance() < CourseRole.STUDENT.getClearance()){
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
        var userRole = userCourseService.getUserCourseRole(courseId, user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (!user.getIsSuperuser() && userRole.getClearance() < CourseRole.STUDENT.getClearance()) {
            throw new ForbiddenException(user);
        }

        if (userRole.getClearance().equals(CourseRole.STUDENT.getClearance())) {
            return trialService.getTrialsByCourseId(courseId, false);
        }

        return trialService.getTrialsByCourseId(courseId, true);
    }

    @PutMapping("/{trialId}/similarity")
    private void generateSimilarityReport(@RequestHeader(name = "User") UserDto user, @PathVariable UUID trialId) {
        var trial = trialService.getTrialById(trialId)
                .orElseThrow(() -> new NotFoundException(trialId.toString()));
        var courseRole = userCourseService.getUserCourseRole(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        var exercises = exerciseService.getExercisesFromTrial(trial, true);

        if (!user.getIsSuperuser() && !courseRole.hasCollaboratorClearance()){
            throw new ForbiddenException(user);
        }

        exercises.forEach(exercise -> antiPlagiarismService.executeSimilarityTool(exercise));
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

        var userRole = userCourseService.getUserCourseRole(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (userRole.getClearance() < CourseRole.COLLABORATOR.getClearance()) {
            throw new ForbiddenException(user);
        }

        trialService.deleteTrial(trial);
    }
}
