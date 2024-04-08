package eu.mostserene.avogador.storageservice.projects;

import eu.mostserene.avogador.storageservice.amqp.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;

@RestController
@RequestMapping("/courses/{courseId}/projects/{projectId}")
public class ProjectController {

    @Autowired
    private Sender sender;

    @PostMapping("/submissions/{submissionId}")
    private void createProjectSubmission(@PathVariable UUID courseId, @PathVariable UUID projectId,
                                         @PathVariable UUID submissionId, @RequestBody MultipartFile file) {
        ProjectStorageImpl.of(courseId, projectId)
                .saveSubmission(submissionId, file);

        sender.send("exercises", "projects.submission.save", new ProjectSubmissionDTO(
                courseId, projectId, submissionId
        ));
    }

    @PutMapping("/submissions/{submissionId}")
    private void addFileToSubmission(@PathVariable UUID courseId, @PathVariable UUID projectId,
                                     @PathVariable UUID submissionId,
                                     @RequestParam String filename,
                                     @RequestBody MultipartFile file) {
        ProjectStorageImpl.of(courseId, projectId)
                .addFileToSubmission(submissionId, filename, file);
    }


    @GetMapping("/submissions/{submissionId}")
    private ResponseEntity<Resource> getProjectSubmission(@PathVariable UUID courseId,
                                                          @PathVariable UUID projectId,
                                                          @PathVariable UUID submissionId) {
        File submissionArchive = ProjectStorageImpl.of(courseId, projectId)
                .getSubmission(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission " + submissionId + " not found"));

        try {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"submission.tar.gz\"")
                    .body(new InputStreamResource(new FileInputStream(submissionArchive)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/submissions/{submissionId}/extra")
    private ResponseEntity<Resource> getSubmissionAdditionalFile(@PathVariable UUID courseId, @PathVariable UUID projectId,
                                                                 @PathVariable UUID submissionId, @RequestParam String filename) {
        File submissionArchive = ProjectStorageImpl.of(courseId, projectId)
                .getAdditionalFile(submissionId, filename)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission " + submissionId + " not found"));

        try {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(new InputStreamResource(new FileInputStream(submissionArchive)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
