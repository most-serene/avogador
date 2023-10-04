package eu.mostserene.avogador.filesystemservice.testcases;

import eu.mostserene.avogador.filesystemservice.exercises.ExerciseStorage;
import eu.mostserene.avogador.filesystemservice.exercises.ExerciseStorageImpl;
import eu.mostserene.avogador.filesystemservice.utils.FileNotFoundException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/courses/{courseId}/trials/{trialId}/exercises/{exerciseId}")
public class TestcaseController {

    @GetMapping("/testcases/{testcaseId}")
    private TestcaseResponseTDO getTestcase(@PathVariable UUID courseId, @PathVariable UUID trialId,
                        @PathVariable UUID exerciseId, @PathVariable UUID testcaseId) {
        return ExerciseStorageImpl.of(courseId, trialId, exerciseId).getTestcase(testcaseId)
                .orElseThrow(() -> new FileNotFoundException("Exercise " + exerciseId + ": Testcase " + testcaseId + " not found"));
    }


    @GetMapping("/testcases")
    private ResponseEntity<Resource> getTestcases(@PathVariable UUID courseId, @PathVariable UUID trialId, @PathVariable UUID exerciseId) {
        ExerciseStorage exerciseStorage = ExerciseStorageImpl.of(courseId, trialId, exerciseId);

        Resource tarResource = new FileSystemResource(exerciseStorage.getTestcases()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise " + exerciseId + ": no testcases")));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"testcases.tar.gz\"")
                .body(tarResource);
    }

}
