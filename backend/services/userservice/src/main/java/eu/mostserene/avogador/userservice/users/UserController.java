package eu.mostserene.avogador.userservice.users;

import eu.mostserene.avogador.userservice.utils.NotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/public/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @GetMapping("/{userId}")
    User getUserById(@PathVariable Long userId) {
        // TODO: AVG-35 - enforce auth
        return userService.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId));
    }

    @GetMapping("/email/{userId}")
    User getUserByEmail(@PathVariable String email) {
        // TODO: AVG-35 - enforce auth
        return userService.getUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User " + email));
    }

    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable Long userId) {
        // TODO: AVG-35 - enforce auth
        userService.deleteUser(userService.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId))
        );
    }

    @GetMapping("/logout")
    void logoutUser(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @PostMapping("/google-auth")
    JSONObject authenticateWithGoogle(HttpServletResponse response, @RequestBody String googleToken) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        JSONObject json = new JSONObject();
        json.put("error", "Auth not implemented, look at AVG-35");
        return json;
    }

}
