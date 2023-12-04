package eu.mostserene.avogador.storageservice.utils;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(String message) {
        super(message);
        log.error(LoggerColors.error(message));
        Sentry.captureException(this);
    }
}
