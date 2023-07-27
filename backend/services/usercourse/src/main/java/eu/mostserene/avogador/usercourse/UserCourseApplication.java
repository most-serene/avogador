package eu.mostserene.avogador.usercourse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@RestController("/")
public class UserCourseApplication {

	@GetMapping("/status")
	String getStatus() {
		return "online";
	}

	public static void main(String[] args) {
		SpringApplication.run(UserCourseApplication.class, args);
	}

}
