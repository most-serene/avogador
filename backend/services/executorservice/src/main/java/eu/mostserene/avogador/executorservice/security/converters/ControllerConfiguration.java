package eu.mostserene.avogador.executorservice.security.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ControllerConfiguration implements WebMvcConfigurer {

    @Autowired
    private UserDTOHeaderConverter userDTOHeaderConverter;

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        registry.addConverter(userDTOHeaderConverter);
        WebMvcConfigurer.super.addFormatters(registry);
    }
}
