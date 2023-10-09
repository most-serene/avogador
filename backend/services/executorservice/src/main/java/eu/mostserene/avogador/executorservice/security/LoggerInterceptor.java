package eu.mostserene.avogador.executorservice.security;

import eu.mostserene.avogador.executorservice.utils.LoggerUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@Slf4j
public class LoggerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(
            @NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler,
            ModelAndView modelAndView) throws Exception {
        if (HttpStatus.valueOf(response.getStatus()).is5xxServerError()) {
            LoggerUtils.logErrorToSentry(request);
        }
    }

}
