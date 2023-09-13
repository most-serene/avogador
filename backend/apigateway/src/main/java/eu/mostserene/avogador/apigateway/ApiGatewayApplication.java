package eu.mostserene.avogador.apigateway;

import eu.mostserene.avogador.apigateway.status.MicroServiceStatus;
import eu.mostserene.avogador.apigateway.status.StatusService;
import eu.mostserene.avogador.apigateway.utils.LoggerColors;
import eu.mostserene.avogador.apigateway.utils.ProfileManager;
import io.sentry.Sentry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController("/")
@Slf4j
public class ApiGatewayApplication {

    @Value("${sentry.dsn}")
    private String sentryDSN;

    @Autowired
    private BuildProperties buildProperties;

    @Autowired
    private StatusService statusService;

    @Autowired
    private ProfileManager profileManager;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("users", r -> r.path("/users/**")
                        .filters(f -> f.rewritePath("/users/(?<segment>.*)", "/public/users/${segment}"))
                        .uri("http://users"))
                .route("course", r -> r.path("/courses")
                        .filters(f -> f.rewritePath("/courses", "/public/courses"))
                        .uri("http://courses"))
                .route("courses", r -> r.path("/courses/**")
                        .filters(f -> f.rewritePath("/courses/(?<segment>)", "/public/courses/${segment}"))
                        .uri("http://courses"))
                .route("users-api", r -> r.path("/api/users/**")
                        .filters(f -> f.rewritePath("/api/users/(?<segment>.*)", "/public/users/${segment}"))
                        .uri("http://users"))
                .route("courses-api", r -> r.path("/api/courses/**")
                        .filters(f -> f.rewritePath("/api/courses/(?<segment>.*)", "/public/courses/${segment}"))
                        .uri("http://courses"))
                .build();
    }

    @GetMapping("/")
    String getInfo() {
        return "Avogador ApiGateway - " + buildProperties.getVersion();
    }

    @GetMapping("/status")
    List<MicroServiceStatus> getStatus() {
        return statusService.getMicroservicesStatus();
    }

    @PostConstruct
    private void postConstruct() {
        String profile = profileManager.getActiveProfiles();

        if ("staging".equals(profile) || "production".equals(profile)) {
            Sentry.init(options -> {
                options.setDsn(sentryDSN);
                options.setServerName(buildProperties.getName());
                options.setRelease(buildProperties.getVersion());
                options.setAttachStacktrace(true);
                options.setEnvironment(profileManager.getActiveProfiles());
            });
        } else {
            log.info(LoggerColors.warn("Remote Logger not active on develop - testing modes"));
        }
    }

    @PreDestroy
    private void closeSentry() {
        Sentry.close();
        log.info(LoggerColors.cyan("Sentry closed"));
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
        log.info("\n\n\t> ApiGateway started\n");
    }

}
