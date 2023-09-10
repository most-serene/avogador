package eu.mostserene.avogador.filesystemservice;

import eu.mostserene.avogador.filesystemservice.utils.LoggerColors;
import eu.mostserene.avogador.filesystemservice.utils.ProfileManager;
import io.sentry.Sentry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;

@SpringBootApplication
@Slf4j
public class FilesystemserviceApplication {

	@Value("${sentry.dsn}")
	private String sentryDSN;

	@Autowired
	private BuildProperties buildProperties;

	@Autowired
	private ProfileManager profileManager;

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
		SpringApplication.run(FilesystemserviceApplication.class, args);
		log.info("\n\n\t> FileSystemService started\n");
	}

}
