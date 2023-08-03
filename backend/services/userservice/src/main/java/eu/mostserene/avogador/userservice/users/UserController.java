package eu.mostserene.avogador.userservice.users;

import com.google.common.hash.Hashing;
import eu.mostserene.avogador.userservice.security.AuthService;
import eu.mostserene.avogador.userservice.security.ForbiddenException;
import eu.mostserene.avogador.userservice.security.InvalidDomainException;
import eu.mostserene.avogador.userservice.utils.NotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/public/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @GetMapping("/{userId}")
    private User getUserById(@PathVariable Long userId) {
        // TODO: AVG-35 - enforce auth
        return userService.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId));
    }

    @GetMapping("/email/{userId}")
    private User getUserByEmail(@PathVariable String email) {
        // TODO: AVG-35 - enforce auth
        return userService.getUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User " + email));
    }

    @DeleteMapping("/{userId}")
    private void deleteUser(@PathVariable Long userId) {
        // TODO: AVG-35 - enforce auth
        userService.deleteUser(userService.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId))
        );
    }

    @PostMapping("/google-auth")
    private AuthUserDTOImageHash authenticateWithGoogle(HttpServletResponse response, @RequestBody GoogleToken googleToken) throws InvalidDomainException {
        log.info("token: "  + googleToken.getGoogleToken());
        AuthService.GoogleUser googleUser = authService.getGoogleUser(googleToken.getGoogleToken());
        Optional<User> queriedUser = userService.getUserByEmail(googleUser.email());

        final User user = queriedUser.map(innerUser -> {
            if (innerUser.getGivenName().equals(googleUser.givenName()) &&
                    innerUser.getFamilyName().equals(googleUser.familyName())) {
                return innerUser;
            }
            innerUser.setGivenName(googleUser.givenName());
            innerUser.setFamilyName(googleUser.familyName());
            return userService.updateUser(innerUser);
        }).orElseGet(() -> userService.createUser(new User(
                googleUser.email(),
                googleUser.givenName(),
                googleUser.familyName()
        )));

        ResponseCookie.ResponseCookieBuilder jwtBuilder = ResponseCookie.from("jwt", authService.generateJWT(user, 0))
                .httpOnly(true)
                .path("/")
                .sameSite("Strict");

        ResponseCookie jwtCookie = ("dev".equals(activeProfile)) ? jwtBuilder.build() : jwtBuilder.secure(true).build();
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        String jwtHash = Hashing.sha256()
                .hashString(jwtCookie.getValue(), StandardCharsets.UTF_8)
                .toString();

        return new AuthUserDTOImageHash(user.generateAuthUserDTO(), googleUser.picture(),
                jwtHash.substring(jwtHash.length() - 20));
    }

    @GetMapping("/logout")
    private void logoutUser(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /**
     * Revokes all the existing jwt for the user in param.
     * This call is allowed only from a superuser or the user himself
     * @param request the request
     * @param userId the id of the user whose tokens have to be revoked
     */
    @PatchMapping("/{userId}/revoke-jwt")
    private void revokeJWTs(HttpServletRequest request, @PathVariable Long userId) {
        AuthUserDTO requestUser = authService.getRequestUser(request);
        if (!requestUser.getIsSuperuser() && !Objects.equals(requestUser.getId(), userId)) {
            throw new ForbiddenException(requestUser);
        }
        authService.revokeUserJWTs(userId);
    }


    private static class GoogleToken {
        private String googleToken;

        public String getGoogleToken() {
            return googleToken;
        }
    }

    private static class AuthUserDTOImageHash extends AuthUserDTO {
        private String picture;

        private String hash;


        public AuthUserDTOImageHash(Long id, String email, String givenName, String familyName,
                                    Boolean isProfessor, Boolean isSuperuser, String picture, String hash) {
            super(id, email, givenName, familyName, isProfessor, isSuperuser);
            this.setPicture(picture);
            this.setHash(hash);
        }

        public AuthUserDTOImageHash(AuthUserDTO authUserDTO, String picture, String hash) {
            super(authUserDTO.getId(), authUserDTO.getEmail(), authUserDTO.getGivenName(), authUserDTO.getFamilyName(),
                    authUserDTO.getIsProfessor(), authUserDTO.getIsSuperuser());
            this.setPicture(picture);
            this.setHash(hash);
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }
    }
}
