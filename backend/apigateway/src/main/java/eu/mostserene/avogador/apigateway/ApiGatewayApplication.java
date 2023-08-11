package eu.mostserene.avogador.apigateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController("/")
@Slf4j
public class ApiGatewayApplication {

	@Autowired
	private BuildProperties buildProperties;

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("users", r -> r.path("/users/**")
						.filters(f -> f.rewritePath("/users/(?<segment>.*)", "/public/users/${segment}"))
						.uri("http://users"))
				.route("course", r -> r.path("/courses/**")
						.filters(f -> f.rewritePath("/courses/(?<segment>.*)", "/public/courses/${segment}"))
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
	String getStatus() {
		return "gateway online";
	}

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
		log.info("\n\n\t> ApiGateway started\n");
	}

}
