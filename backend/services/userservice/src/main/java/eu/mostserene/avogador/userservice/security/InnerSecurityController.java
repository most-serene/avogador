package eu.mostserene.avogador.userservice.security;

import eu.mostserene.avogador.userservice.users.AuthUserDTO;
import eu.mostserene.avogador.userservice.users.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security")
public class InnerSecurityController {

    @GetMapping("/validate-jwt")
    private AuthUserDTO validateJWT(HttpServletRequest request) {
        return null;
    }
}
