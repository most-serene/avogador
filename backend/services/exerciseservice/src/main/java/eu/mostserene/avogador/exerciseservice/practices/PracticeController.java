package eu.mostserene.avogador.exerciseservice.practices;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/public/trials/practices")
@Slf4j
public class PracticeController {

    /**
     * Returns the practice by ID
     * @param user the requesting user
     * @param practiceId the id of the practice
     * @return the practice
     */
    @GetMapping("/{practiceId}")
    private Practice getPracticeById(@RequestHeader(name = "User") UserDto user, @PathVariable UUID practiceId) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a practice
     * @param user the requesting user
     * @param practice the practice
     * @return the created practice
     */
    @PostMapping("")
    private Practice createPractice(@RequestHeader(name = "User") UserDto user, @RequestBody Practice practice) {
        throw new UnsupportedOperationException();
    }


    /**
     * Updates a practice given the id
     * @param user the requesting user
     * @param practiceId the id of the practice
     * @param practice the updated practice
     * @return the saved updated practice
     */
    @PutMapping("/{practiceId}")
    private Practice updatePractice(@RequestHeader(name = "User") UserDto user, @PathVariable UUID practiceId, @RequestBody Practice practice) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the list of the exercises of a practice
     * @param user the requesting user
     * @param practiceId the id of the practice
     * @return the list of the exercises of a practice
     */
    @GetMapping("/{practiceId}/exercises")
    private List<Exercise> getPracticeExercises(@RequestHeader(name = "User") UserDto user, @PathVariable UUID practiceId) {
        throw new UnsupportedOperationException();
    }

    /**
     * Deletes a practice by id
     * @param user the requesting user
     * @param practiceId the id of the practice
     */
    @DeleteMapping("/{practiceId}")
    private void deletePractice(@RequestHeader(name = "User") UserDto user, @PathVariable UUID practiceId) {
        throw new UnsupportedOperationException();
    }
}

