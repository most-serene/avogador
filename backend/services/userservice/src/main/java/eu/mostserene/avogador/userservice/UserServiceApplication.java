package eu.mostserene.avogador.userservice;

import eu.mostserene.avogador.userservice.profilemanager.ExecutionProfile;
import eu.mostserene.avogador.userservice.security.AuthService;
import eu.mostserene.avogador.userservice.utils.LoggerColors;
import io.sentry.Sentry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController("/")
@Slf4j
public class UserServiceApplication {

	@Value("${sentry.dsn}")
	private String sentryDSN;

	@Autowired
	private AuthService authService;

	@Autowired
	private BuildProperties buildProperties;

	@Autowired
	private ExecutionProfile executionProfile;

	@Value("${spring.profiles.active}")
	private String activeProfileName;


	@GetMapping("/public/users/status")
	String getStatus() {
		return "online";
	}

	@PostConstruct
	private void postConstruct() {
		if ("staging".equals(activeProfileName) || "production".equals(activeProfileName)) {
			Sentry.init(options -> {
				options.setDsn(sentryDSN);
				options.setServerName(buildProperties.getName());
				options.setRelease(buildProperties.getVersion());
				options.setAttachStacktrace(true);
				options.setEnvironment(activeProfileName);
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
		SpringApplication.run(UserServiceApplication.class, args);
		log.info("\n\n\t> Server started\n");
	}

}
