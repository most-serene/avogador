package eu.mostserene.avogador.filesystemservice.utils;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileNotFound extends RuntimeException {
    public FileNotFound(String message) {
        super(message);
        log.error(LoggerColors.error(message));
        Sentry.captureException(this);
    }
}
