package eu.mostserene.avogador.usercourse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class UserCourseApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserCourseApplication.class, args);
	}

}
