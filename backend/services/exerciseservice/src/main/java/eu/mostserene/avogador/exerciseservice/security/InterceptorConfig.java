package eu.mostserene.avogador.exerciseservice.security;

import eu.mostserene.avogador.exerciseservice.security.restapicontrol.RequestSourceInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class InterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private LoggerInterceptor loggerInterceptor;

    @Autowired
    private RequestSourceInterceptor requestSourceInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggerInterceptor);
        registry.addInterceptor(requestSourceInterceptor);
    }
}