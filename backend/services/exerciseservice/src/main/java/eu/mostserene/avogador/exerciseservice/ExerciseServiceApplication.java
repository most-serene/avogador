package eu.mostserene.avogador.exerciseservice;

import eu.mostserene.avogador.exerciseservice.utils.LoggerColors;
import eu.mostserene.avogador.exerciseservice.utils.ProfileManager;
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
@Slf4j
@RestController("/")
public class ExerciseServiceApplication {

	@Value("${sentry.dsn}")
	private String sentryDSN;

	@Autowired
	private BuildProperties buildProperties;

	@Autowired
	private ProfileManager profileManager;

	@GetMapping("/public/exercises/status")
	String getStatus() {
		return "online";
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
		SpringApplication.run(ExerciseServiceApplication.class, args);
		log.info("\n\n\t> ExerciseService started\n");
	}

}
