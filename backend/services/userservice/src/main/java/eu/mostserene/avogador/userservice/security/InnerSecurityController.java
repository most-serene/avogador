package eu.mostserene.avogador.userservice.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.userservice.users.AuthUserDTO;
import lombok.Data;
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

}
