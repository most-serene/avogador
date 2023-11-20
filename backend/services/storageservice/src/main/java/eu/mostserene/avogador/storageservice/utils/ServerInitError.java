package eu.mostserene.avogador.storageservice.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerInitError extends Error {
    public ServerInitError(String message) {
        super(message);
        log.error(LoggerColors.error(message));
    }
}
