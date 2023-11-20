package eu.mostserene.avogador.storageservice.exercises;

import eu.mostserene.avogador.storageservice.strox.Strox;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/courses/{courseId}/trials/{trialId}/exercises/{exerciseId}")
public class ExerciseController {

    @GetMapping("/template")
    private Strox getExerciseStroxTemplate(@PathVariable UUID courseId, @PathVariable UUID trialId, @PathVariable UUID exerciseId) {
        Strox template = ExerciseStorageImpl.of(courseId, trialId, exerciseId).getTemplate()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Template of exercise " + exerciseId + " not found"));
        template.setPath(null);
        return template;
    }
}
