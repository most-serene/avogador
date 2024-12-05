package eu.mostserene.avogador.executorservice.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * <a href="https://knowyourmeme.com/memes/booker-catch">...</a>
 */
@Slf4j
public final class BookerCatcher<T extends Exception> {
    private T exception;

    public BookerCatcher() {
        exception = null;
    }

    public void catchException(T exception) {
        this.exception = exception;
    }

    public Optional<T> get() {
        return exception == null ? Optional.empty() : Optional.of(exception);
    }

    public void throwIfPresent() throws T {
        if (exception != null) {
            throw exception;
        }
    }

    public boolean hasBeenCaught() {
        return exception != null;
    }
}
