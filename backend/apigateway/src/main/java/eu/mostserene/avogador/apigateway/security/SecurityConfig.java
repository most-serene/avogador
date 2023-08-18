package eu.mostserene.avogador.apigateway.security;

import eu.mostserene.avogador.apigateway.utils.ProfileManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.util.List;

@Configuration
@EnableWebFlux
public class SecurityConfig {

    @Value("${security.allowed.origins}")
    private String allowedOrigins;
    @Autowired
    private ProfileManager profileManager;

    @Bean
    @Order(1)
    CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", profileManager.executeOnProfile(
                this::relaxedCORS,
                this::relaxedCORS,
                this::relaxedCORS,
                this::productionCORS,
                corsConfig
        ));

        return new CorsWebFilter(source);
    }

    private CorsConfiguration relaxedCORS(CorsConfiguration c) {
        c.setAllowedOriginPatterns(List.of("*"));
        //corsConfig.setMaxAge("*");
        c.addAllowedMethod(HttpMethod.HEAD);
        c.addAllowedMethod(HttpMethod.GET);
        c.addAllowedMethod(HttpMethod.POST);
        c.addAllowedMethod(HttpMethod.PUT);
        c.addAllowedMethod(HttpMethod.DELETE);
        c.addAllowedMethod(HttpMethod.PATCH);
        c.setAllowCredentials(true);
        c.addAllowedHeader("*");
        return c;
    }


    private CorsConfiguration productionCORS(CorsConfiguration c) {
        c.setAllowedOrigins(List.of(StringUtils.splitPreserveAllTokens(allowedOrigins, ',')));
        //corsConfig.setMaxAge("*");
        c.addAllowedMethod("");
        c.addAllowedMethod(HttpMethod.HEAD);
        c.addAllowedMethod(HttpMethod.GET);
        c.addAllowedMethod(HttpMethod.POST);
        c.addAllowedMethod(HttpMethod.PUT);
        c.addAllowedMethod(HttpMethod.DELETE);
        c.addAllowedMethod(HttpMethod.PATCH);
        c.setAllowCredentials(true);
        c.addAllowedHeader("*");
        return c;
    }
}

