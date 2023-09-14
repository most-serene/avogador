package eu.mostserene.avogador.userservice.security.restapicontrol;

import eu.mostserene.avogador.userservice.utils.LoggerColors;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@Slf4j
public class RequestSourceAspect {

    @Pointcut("@annotation(DisablePublicRestAPI)")
    public void executePointcut() {
    }

    @Around("executePointcut()")
    public Object aroundExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        RequestSource requestSource = RequestSource.valueOfLabel(request.getHeader("Source"));

        if (RequestSource.REST_API.equals(requestSource)) {
            log.info(LoggerColors.warn("A request to " + request.getRequestURI() +
                    " has been blocked since its source is the public rest API"));
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return joinPoint.proceed();
    }
}
