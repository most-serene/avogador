package eu.mostserene.avogador.userservice.security;

import eu.mostserene.avogador.userservice.users.AuthUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/security")
public class InnerSecurityController {

    @Autowired
    private AuthService authService;

    @PostMapping("/validate-jwt")
    private AuthUserDTO validateJWT(@RequestBody String jwtToValidate) {
        return authService.decodeJwt(jwtToValidate);
    }

    @PostMapping("/validate-key")
    private AuthUserDTO validateApiKey(@RequestBody String apiKey) {
        return authService.validateApiKey(apiKey);
    }

}
