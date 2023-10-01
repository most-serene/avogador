package eu.mostserene.avogador.filesystemservice.submission;

import eu.mostserene.avogador.filesystemservice.exercises.ExerciseStorage;
import eu.mostserene.avogador.filesystemservice.exercises.ExerciseStorageImpl;
import eu.mostserene.avogador.filesystemservice.strox.Strox;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/courses/{courseId}/trials/{trialId}/exercises/{exerciseId}/submissions/{submissionId}")
public class SubmissionController {

    @GetMapping("/source")
    private ResponseEntity<Resource> getSourceCode(@PathVariable UUID courseId, @PathVariable UUID trialId,
                                            @PathVariable UUID exerciseId, @PathVariable UUID submissionId) {

        ExerciseStorage exerciseStorage = ExerciseStorageImpl.of(courseId, trialId, exerciseId);

        Resource tarResource = new FileSystemResource(exerciseStorage.getSubmissionCode(submissionId));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"submission.tar.gz\"")
                .body(tarResource);
    }

    @GetMapping("/strox")
    private Strox getStroxSubmission(@PathVariable UUID courseId, @PathVariable UUID trialId, @PathVariable UUID exerciseId, @PathVariable UUID submissionId) {
        Strox submission = ExerciseStorageImpl.of(courseId, trialId, exerciseId)
                .getSubmissionStrox(submissionId);
        submission.setPath(null);
        return submission;
    }
}
