package eu.mostserene.avogador.filesystemservice.exercises;

import eu.mostserene.avogador.filesystemservice.strox.Strox;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/courses/{courseId}/trials/{trialId}/exercises/{exerciseId}")
public class ExerciseController {

    @GetMapping("/template")
    private Strox getExerciseStroxTemplate(@PathVariable UUID courseId, @PathVariable UUID trialId, @PathVariable UUID exerciseId) {
        Strox template = ExerciseStorageImpl.of(courseId, trialId, exerciseId).getTemplate();
        template.setPath(null);
        return template;
    }
}
