package eu.mostserene.avogador.exerciseservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import io.sentry.Scope;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import io.sentry.protocol.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggerUtils {

    private LoggerUtils() {}

    public static void logErrorToSentry(Exception exception, HttpServletRequest request) {
        Sentry.captureException(exception,
                scope -> {
                    scope.setContexts("Endpoint", request.getRequestURL().toString());
                    if (request.getHeader("User") != null) {
                        setRequestUser(request, scope);
                    }
                    scope.setContexts("Query", request.getQueryString());
                    scope.setLevel(SentryLevel.ERROR);
                });
    }

    public static void logErrorToSentry(HttpServletRequest request) {
        Sentry.captureMessage("An internal server error has occurred",
                scope -> {
                    scope.setContexts("Endpoint", request.getRequestURL().toString());
                    if (request.getHeader("User") != null) {
                        setRequestUser(request, scope);
                    }
                    scope.setContexts("Query", request.getQueryString());
                    scope.setLevel(SentryLevel.ERROR);
                });
    }

    private static void setRequestUser(HttpServletRequest request, Scope scope) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            UserDto userDto = mapper.readValue(request.getHeader("User"), UserDto.class);
            User sentryUser = new User();
            sentryUser.setId(userDto.getId().toString());
            sentryUser.setEmail(userDto.getEmail());
            sentryUser.setUsername(userDto.getGivenName() + " " + userDto.getFamilyName());
            sentryUser.setIpAddress(request.getRemoteAddr());
            scope.setUser(sentryUser);
        } catch (JsonProcessingException e) {
            log.error(LoggerColors.error(e.toString()));
            Sentry.captureException(e);
        }
    }
}
