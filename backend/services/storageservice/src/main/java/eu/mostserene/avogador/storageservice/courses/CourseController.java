package eu.mostserene.avogador.storageservice.courses;

import eu.mostserene.avogador.storageservice.exercises.ExerciseStorage;
import eu.mostserene.avogador.storageservice.exercises.ExerciseStorageImpl;
import eu.mostserene.avogador.storageservice.strox.Strox;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/courses/{courseId}")
public class CourseController {

    @GetMapping("/archive")
    private ResponseEntity<Resource> getCourseArchive(@PathVariable UUID courseId) {
        CourseStorage courseStorage = CourseStorageImpl.of(courseId);

        Resource tarResource = new FileSystemResource(courseStorage.getArchive()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course " + courseId + " archive not found")));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"submissions.tar.gz\"")
                .body(tarResource);
    }

}