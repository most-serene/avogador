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
                .route("websocket", r -> r.path("/ws")
                        .uri("ws://users"))
                .route("websockets", r -> r.path("/ws/**")
                        .uri("ws://users"))
                .route("user", r -> r.path("/users")
                        .filters(f -> f.rewritePath("/users", "/public/users"))
                        .uri("http://users"))
                .route("users", r -> r.path("/users/**")
                        .filters(f -> f.rewritePath("/users/(?<segment>.*)", "/public/users/${segment}"))
                        .uri("http://users"))
                .route("course", r -> r.path("/courses")
                        .filters(f -> f.rewritePath("/courses", "/public/courses"))
                        .uri("http://courses"))
                .route("courses", r -> r.path("/courses/**")
                        .filters(f -> f.rewritePath("/courses/(?<segment>)", "/public/courses/${segment}"))
                        .uri("http://courses"))
                .route("trial", r -> r.path("/trials")
                        .filters(f -> f.rewritePath("/trials", "/public/trials"))
                        .uri("http://exercises"))
                .route("trials", r -> r.path("/trials/**")
                        .filters(f -> f.rewritePath("/trials/(?<segment>)", "/public/trials/${segment}"))
                        .uri("http://exercises"))
                .route("exercise", r -> r.path("/exercises")
                        .filters(f -> f.rewritePath("/exercises", "/public/exercises"))
                        .uri("http://exercises"))
                .route("exercises", r -> r.path("/exercises/**")
                        .filters(f -> f.rewritePath("/exercises/(?<segment>)", "/public/exercises/${segment}"))
                        .uri("http://exercises"))
                .route("project", r -> r.path("/projects")
                        .filters(f -> f.rewritePath("/projects", "/public/projects"))
                        .uri("http://exercises"))
                .route("projects", r -> r.path("/projects/**")
                        .filters(f -> f.rewritePath("/projects/(?<segment>)", "/public/projects/${segment}"))
                        .uri("http://exercises"))
                .route("users-api", r -> r.path("/api/users/**")
                        .filters(f -> f.rewritePath("/api/users/(?<segment>.*)", "/public/users/${segment}"))
                        .uri("http://users"))
                .route("course-api", r -> r.path("/api/courses")
                        .filters(f -> f.rewritePath("/api/courses", "/public/courses"))
                        .uri("http://courses"))
                .route("courses-api", r -> r.path("/api/courses/**")
                        .filters(f -> f.rewritePath("/api/courses/(?<segment>.*)", "/public/courses/${segment}"))
                        .uri("http://courses"))
                .route("trial-api", r -> r.path("/api/trials")
                        .filters(f -> f.rewritePath("/api/trials", "/public/trials"))
                        .uri("http://exercises"))
                .route("trials-api", r -> r.path("/api/trials/**")
                        .filters(f -> f.rewritePath("/api/trials/(?<segment>)", "/public/trials/${segment}"))
                        .uri("http://exercises"))
                .route("exercise-api", r -> r.path("/api/exercises")
                        .filters(f -> f.rewritePath("/api/exercises", "/public/exercises"))
                        .uri("http://exercises"))
                .route("exercises-api", r -> r.path("/api/exercises/**")
                        .filters(f -> f.rewritePath("/api/exercises/(?<segment>)", "/public/exercises/${segment}"))
                        .uri("http://exercises"))
                .route("projects-api", r -> r.path("/api/projects")
                        .filters(f -> f.rewritePath("/api/projects", "/public/projects"))
                        .uri("http://exercises"))
                .route("projects-api", r -> r.path("/api/projects/**")
                        .filters(f -> f.rewritePath("/api/projects/(?<segment>)", "/public/projects/${segment}"))
                        .uri("http://exercises"))

                .route("analytics", r -> r.path("/analytics/**")
                        .filters(f -> f.rewritePath("/analytics/(?<segment>)", "/public/analytics/${segment}"))
                        .uri("http://exercises"))
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

    @GetMapping("/version/webapp")
    String getWebappVersion() {
        return "0.15.0";
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
