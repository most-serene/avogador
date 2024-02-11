package eu.mostserene.avogador.storageservice.trials;

import eu.mostserene.avogador.storageservice.utils.LoggerColors;
import eu.mostserene.avogador.storageservice.utils.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/courses/{courseId}/trials/{trialId}")
@Slf4j
public class TrialController {

    @GetMapping("/logs")
    private List<String> getTrialLogs(@PathVariable UUID courseId, @PathVariable UUID trialId) {
        try {
            return TrialStorageImpl.of(courseId, trialId).getLogs();
        } catch (IOException e) {
            log.error(LoggerColors.error(e.getMessage()));
            LoggerUtils.logErrorToSentry(e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
