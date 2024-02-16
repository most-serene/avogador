package eu.mostserene.avogador.storageservice.submission;

import eu.mostserene.avogador.storageservice.exercises.ExerciseStorage;
import eu.mostserene.avogador.storageservice.exercises.ExerciseStorageImpl;
import eu.mostserene.avogador.storageservice.strox.Strox;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/courses/{courseId}/trials/{trialId}/exercises/{exerciseId}/submissions")
public class SubmissionController {

    @PatchMapping("/source")
    private ResponseEntity<Resource> getSubmissions(@PathVariable UUID courseId, @PathVariable UUID trialId,
                                                    @PathVariable UUID exerciseId, @RequestBody List<UUID> submissionIds) {

        ExerciseStorage exerciseStorage = ExerciseStorageImpl.of(courseId, trialId, exerciseId);

        Resource tarResource = new FileSystemResource(exerciseStorage.getSubmissionsCode(submissionIds));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"submissions.tar.gz\"")
                .body(tarResource);
    }

    @GetMapping("/{submissionId}/source")
    private ResponseEntity<Resource> getSourceCode(@PathVariable UUID courseId, @PathVariable UUID trialId,
                                                   @PathVariable UUID exerciseId, @PathVariable UUID submissionId) {
        ExerciseStorage exerciseStorage = ExerciseStorageImpl.of(courseId, trialId, exerciseId);

        File submissionCode = exerciseStorage.getSubmissionCode(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission " + submissionId + " code not found"));

        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(submissionCode.toPath(), StandardOpenOption.DELETE_ON_CLOSE);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Submission " + submissionId + " an internal server error has occurred while exporting the code ");
        }

        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        FileUtils.deleteQuietly(submissionCode);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"submission.tar.gz\"")
                .body(inputStreamResource);
    }

    @GetMapping("/{submissionId}/strox")
    private Strox getStroxSubmission(@PathVariable UUID courseId, @PathVariable UUID trialId, @PathVariable UUID exerciseId, @PathVariable UUID submissionId) {
        Strox submission = ExerciseStorageImpl.of(courseId, trialId, exerciseId)
                .getSubmissionStrox(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission " + submissionId + " strox not found"));
        submission.setPath(null);
        return submission;
    }
}
