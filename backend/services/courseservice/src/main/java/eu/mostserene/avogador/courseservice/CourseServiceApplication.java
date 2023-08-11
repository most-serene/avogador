package eu.mostserene.avogador.courseservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController("/")
@Slf4j
public class CourseServiceApplication {

	@GetMapping("/public/courses/status")
	String getStatus() {
		return "courseService online";
	}

	public static void main(String[] args) {
		SpringApplication.run(CourseServiceApplication.class, args);
		log.info("\n\n\t> CourseService started\n");
	}

}
