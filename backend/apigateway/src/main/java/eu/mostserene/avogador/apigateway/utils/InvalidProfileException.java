package eu.mostserene.avogador.apigateway.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * Thrown if the provided Profile is invalid
 */
@Slf4j
public class InvalidProfileException extends Exception {
    public InvalidProfileException() {
        super("----- NO PROFILE HAS BEEN PROVIDED -----");
        log.error("----- NO PROFILE HAS BEEN PROVIDED -----");
    }
}
