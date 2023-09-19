package eu.mostserene.avogador.exerciseservice.practices;

import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
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

        if (user.getIsSuperuser()) return practice;

        CourseRole courseRole = userCourseService.getUserCourseRole(practice.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if ((courseRole.getClearance() > CourseRole.STUDENT.getClearance()) ||
                (CourseRole.STUDENT.getClearance().equals(courseRole.getClearance()) &&
                        practice.getIsVisible() && practice.getIsPublic())
        ) return practice;

        throw new ForbiddenException(user);
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

        if (user.getIsSuperuser() || courseRole.getClearance() > CourseRole.STUDENT.getClearance()) {
            return practiceService.createOrUpdatePractice(practice);
        }
        throw new ForbiddenException(user);
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
        var storedPractice = practiceService.getPractice(practiceId);
        if (storedPractice.isEmpty()) throw new NotFoundException(practiceId.toString());

        CourseRole courseRole = userCourseService.getUserCourseRole(practice.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (user.getIsSuperuser() || courseRole.getClearance() > CourseRole.STUDENT.getClearance()) {
            return practiceService.createOrUpdatePractice(practice);
        }
        throw new ForbiddenException(user);
    }

    /**
     * Returns the list of the exercises of a practice
     *
     * @param user       the requesting user
     * @param practiceId the id of the practice
     * @return the list of the exercises of a practice
     */
    @GetMapping("/{practiceId}/exercises")
    private List<Exercise> getPracticeExercises(@RequestHeader(name = "User") UserDto user, @PathVariable UUID practiceId) {
        throw new UnsupportedOperationException();
    }

    /**
     * Deletes a practice by id
     *
     * @param user       the requesting user
     * @param practiceId the id of the practice
     */
    @DeleteMapping("/{practiceId}")
    private void deletePractice(@RequestHeader(name = "User") UserDto user, @PathVariable UUID practiceId) {
        Practice practice = practiceService.getPractice(practiceId)
                .orElseThrow(NotFoundException::new);

        CourseRole courseRole = userCourseService.getUserCourseRole(practice.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (user.getIsSuperuser() || courseRole.getClearance() > CourseRole.STUDENT.getClearance()) {
            practiceService.deletePractice(practice);
            return;
        }
        throw new ForbiddenException(user);
    }
}

