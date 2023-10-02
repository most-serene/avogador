package eu.mostserene.avogador.exerciseservice.practices;

import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrial;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialService;
import eu.mostserene.avogador.exerciseservice.utils.BadRequestException;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/public/trials/practices")
@Slf4j
public class PracticeController {

    @Autowired
    private PracticeService practiceService;

    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private UserTrialService userTrialService;

    /**
     * Returns the practice by ID
     *
     * @param user       the requesting user
     * @param practiceId the id of the practice
     * @return the practice
     */
    @GetMapping("/{practiceId}")
    private Practice getPracticeById(@RequestHeader(name = "User") UserDto user, @PathVariable UUID practiceId) {
        Practice practice = practiceService.getPractice(practiceId)
                .orElseThrow(NotFoundException::new);

        CourseRole courseRole = userCourseService.getUserCourseRole(practice.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (user.getIsSuperuser()) return practice;

        if ((courseRole.getClearance() < CourseRole.COLLABORATOR.getClearance() && !practice.getIsVisible())
                || courseRole.getClearance().equals(CourseRole.EXTERNAL.getClearance())) {
            throw new ForbiddenException(user);
        }

        return practice;
    }

    /**
     * Creates a practice
     *
     * @param user     the requesting user
     * @param practice the practice
     * @return the created practice
     */
    @PostMapping("")
    private Practice createPractice(@RequestHeader(name = "User") UserDto user, @RequestBody Practice practice) {
        CourseRole courseRole = userCourseService.getUserCourseRole(practice.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseRole.getClearance() < CourseRole.COLLABORATOR.getClearance() && !user.getIsSuperuser()) {
            throw new ForbiddenException(user);
        }
        return practiceService.createPractice(practice);
    }

    /**
     * Updates a practice given the id
     *
     * @param user       the requesting user
     * @param practiceId the id of the practice
     * @param practice   the updated practice
     * @return the saved updated practice
     */
    @PutMapping("/{practiceId}")
    private Practice updatePractice(@RequestHeader(name = "User") UserDto user, @PathVariable UUID practiceId, @RequestBody Practice practice) {
        var storedPractice = practiceService.getPractice(practiceId)
                .orElseThrow(() -> new NotFoundException(practiceId.toString()));

        if (!storedPractice.getId().equals(practice.getId())) {
            throw new BadRequestException("Id mismatch");
        }

        CourseRole courseRole = userCourseService.getUserCourseRole(practice.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseRole.getClearance() < CourseRole.COLLABORATOR.getClearance() && !user.getIsSuperuser()) {
            throw new ForbiddenException(user);
        }
        return practiceService.updatePractice(practice);
    }

    /**
     * Returns the list of the exercises of a practice
     *
     * @param user       the requesting user
     * @param practiceId the id of the practice
     * @return the list of the exercises of a practice
     */
    @GetMapping("/{practiceId}/exercises")
    private List<Exercise> getExercisesFromPractice(@RequestHeader(name = "User") UserDto user, @PathVariable UUID practiceId) {
        var practice = practiceService.getPractice(practiceId)
                .orElseThrow(() -> new NotFoundException(practiceId.toString()));
        var courseRole = userCourseService.getUserCourseRole(practice.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        Boolean canSeeHiddenExercises = user.getIsSuperuser() || courseRole.getClearance() >= CourseRole.COLLABORATOR.getClearance();

        return exerciseService.getExercisesFromTrial(practice, canSeeHiddenExercises);
    }

    /**
     * Join a practice
     *
     * @param user       the requesting user
     * @param practiceId the id of practice
     * @return the new UserTrial relation
     */
    @PutMapping("/{practiceId}/join")
    private UserTrial joinPractice(@RequestHeader(name = "User") UserDto user, @PathVariable UUID practiceId) {
        var practice = practiceService.getPractice(practiceId)
                .orElseThrow(() -> new NotFoundException(practiceId.toString()));
        var courseRole = userCourseService.getUserCourseRole(practice.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (user.getIsSuperuser() || courseRole.getClearance() >= CourseRole.COLLABORATOR.getClearance()){
            return null;
        }

        return userTrialService.joinTrial(user, practice);
    }
}

