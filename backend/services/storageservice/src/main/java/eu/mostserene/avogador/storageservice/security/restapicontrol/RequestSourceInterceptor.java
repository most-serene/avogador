package eu.mostserene.avogador.storageservice.security.restapicontrol;

import eu.mostserene.avogador.storageservice.utils.LoggerColors;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class RequestSourceInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) return true;

        boolean isEndpointPublic = handlerMethod.getMethodAnnotation(EnablePublicRestAPI.class) != null;
        if (isEndpointPublic) return true;

        RequestSource requestSource = RequestSource.valueOfLabel(request.getHeader("Source"));

        if (RequestSource.REST_API.equals(requestSource)) {
            log.info(LoggerColors.warn("A request to " + request.getRequestURI() +
                    " has been blocked since its source is the public rest API"));
            response.setStatus(404);
            return false;
        }
        return true;
    }
}

