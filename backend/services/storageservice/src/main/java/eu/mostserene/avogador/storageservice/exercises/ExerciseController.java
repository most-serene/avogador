package eu.mostserene.avogador.storageservice.exercises;

import eu.mostserene.avogador.storageservice.strox.Strox;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/similarity-report")
    private ResponseEntity<Resource> getSimilarityReport(@PathVariable UUID courseId, @PathVariable UUID trialId, @PathVariable UUID exerciseId) {
        Resource zipResource = new FileSystemResource(ExerciseStorageImpl.of(courseId, trialId, exerciseId)
                .getSimilarityReport()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise " + exerciseId + " similarity report not found")));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"similarity.zip\"")
                .body(zipResource);
    }
}
