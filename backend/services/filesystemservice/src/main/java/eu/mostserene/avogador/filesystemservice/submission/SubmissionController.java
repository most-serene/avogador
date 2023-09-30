package eu.mostserene.avogador.filesystemservice.submission;

import eu.mostserene.avogador.filesystemservice.exercises.ExerciseStorageImpl;
import eu.mostserene.avogador.filesystemservice.strox.Strox;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/courses/{courseId}/trials/{trialId}/exercises/{exerciseId}/submissions/{submissionId}")
public class SubmissionController {

    

    @GetMapping("/strox")
    private Strox getStroxSubmission(@PathVariable UUID courseId, @PathVariable UUID trialId, @PathVariable UUID exerciseId, @PathVariable UUID submissionId) {
        Strox submission = ExerciseStorageImpl.of(courseId, trialId, exerciseId)
                .getSubmissionStrox(submissionId);
        submission.setPath(null);
        return submission;
    }
}
