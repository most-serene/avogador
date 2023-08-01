package eu.mostserene.avogador.userservice;

import eu.mostserene.avogador.userservice.security.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController("/")
@Slf4j
public class UserServiceApplication {
	@Autowired
	private AuthService authService;

	@GetMapping("/public/users/status")
	String getStatus(HttpServletRequest request) {
		log.info(authService.getRequestUser(request).getGivenName());
		return "userService online";
	}

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
