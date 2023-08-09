package eu.mostserene.avogador.userservice.users;

import com.google.common.hash.Hashing;
import eu.mostserene.avogador.userservice.apikey.AlreadyExistingKeyException;
import eu.mostserene.avogador.userservice.apikey.ApiKeyDTO;
import eu.mostserene.avogador.userservice.apikey.ApiKeyService;
import eu.mostserene.avogador.userservice.mail.EmailService;
import eu.mostserene.avogador.userservice.security.AuthService;
import eu.mostserene.avogador.userservice.security.ForbiddenException;
import eu.mostserene.avogador.userservice.security.InvalidDomainException;
import eu.mostserene.avogador.userservice.utils.BadRequestException;
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
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
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

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApiKeyService apiKeyService;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    /**
     * Get a user by id, if called by a student, and he's not himself, the email is obfuscated
     * @param request the current request
     * @param userId the id of the user
     * @return the corresponding user
     */
    @GetMapping("/{userId}")
    private AuthUserDTO getUserById(HttpServletRequest request, @PathVariable Long userId) {
        return authService.executeOnRole(request,
                student -> userService.getUserById(userId)
                        .map(user -> {
                            if (!student.getId().equals(userId)) {
                                user.setEmail(null);
                            }
                            return user;
                        }),
                professor -> userService.getUserById(userId),
                superuser -> userService.getUserById(userId)
        ).orElseThrow(() -> new NotFoundException("User " + userId)).generateAuthUserDTO();
    }

    /**
     * Get a user by his email
     * @param request the current request
     * @param email the email of the user
     * @return the corresponding user
     */
    @GetMapping("/email/{userId}")
    private AuthUserDTO getUserByEmail(HttpServletRequest request, @PathVariable String email) {
        authService.getRequestUser(request);
        return userService.getUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User " + email)).generateAuthUserDTO();
    }

    /**
     * Delete a user by id. Only a superuser or the user himself are allowed to do that.
     * @param userId the id of the user
     */
    @DeleteMapping("/{userId}")
    private void deleteUser(HttpServletRequest request, @PathVariable Long userId) {
        AuthUserDTO authUserDTO = authService.getRequestUser(request);

        authUserDTO.requireSuperuser().ifPresentOrElse(superuser ->
            userService.deleteUser(userService.getUserById(userId)
                    .orElseThrow(() -> new NotFoundException("User " + userId))),
                () -> {
                    authUserDTO.requireId(userId)
                            .orElseThrow(() -> new ForbiddenException(authUserDTO));
                    userService.deleteUser(userService.getUserById(userId)
                            .orElseThrow(() -> new NotFoundException("User " + userId)));
        });
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
        }).orElseGet(() -> {
            emailService.sendSimpleEmail(googleUser.email(), "Welcome to Avogador!",
                    "Hi " + googleUser.givenName() + "!\nYou have been successfully registered to Avogador, enjoy!");

            return userService.createUser(new User(
                googleUser.email(),
                googleUser.givenName(),
                googleUser.familyName())
            );
        });

        String cookieName = "develop".equals(activeProfile) ? "jwt" : "__Secure-jwt";

        ResponseCookie.ResponseCookieBuilder jwtBuilder = ResponseCookie.from(cookieName, authService.generateJWT(user, 0))
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("None");

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
        String cookieName = "develop".equals(activeProfile) ? "jwt" : "__Secure-jwt";
        Cookie cookie = new Cookie(cookieName, null);
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

    /**
     * Gets all the API key owned by a user
     * @param request the current request
     * @param userId the id of the user who owns the keys
     * @return the list of the API keys
     */
    @GetMapping("/{userId}/api-key")
    private List<ApiKeyDTO> getUserApiKeys(HttpServletRequest request, @PathVariable Long userId) {
        AuthUserDTO requester = authService.getRequestUser(request);
        requester.requireId(userId).orElseThrow(() -> new ForbiddenException(requester));

        return apiKeyService.getApiKeyByUser(
                userService.getUserById(userId).orElseThrow(() -> new NotFoundException(userId.toString())))
                .stream()
                .map(apiKey -> new ApiKeyDTO(apiKey.getId(), apiKey.getName(),
                        apiKey.getUser().getId(), apiKey.getCreationTimestamp(), apiKey.getExpirationTimestamp()))
                .toList();
    }

    /**
     * Generate an API key for the given user
     * @param request the current request
     * @param userId the id of the user who will own the key
     * @param apiKeyName the firendly name of the key
     * @return the generated key
     * @throws AlreadyExistingKeyException if the pair user-name already exists
     */
    @PostMapping("/{userId}/api-key")
    private String generateApiKey(HttpServletRequest request, @PathVariable Long userId, @RequestBody ApiKeyName apiKeyName) {
        AuthUserDTO requester = authService.getRequestUser(request);
        requester.requireId(userId).orElseThrow(() -> new ForbiddenException(requester));

        if (apiKeyName.getApikeyName().split("\\s+").length > 1) {
            throw new BadRequestException("ApiKey name cannot contain spaces");
        }

        return apiKeyService.generateApiKey(
                userService.getUserById(requester.getId())
                        .orElseThrow(() -> new NotFoundException(userId.toString())),
                apiKeyName.getApikeyName(), apiKeyName.getExpiration()
        );
    }

    /**
     * Delete an API key
     * @param request the current request
     * @param userId the id of the user who owns the key
     * @param keyName the friendly name of the key
     */
    @DeleteMapping("/{userId}/api-key/{keyName}")
    private void deleteApiKey(HttpServletRequest request, @PathVariable Long userId, @PathVariable String keyName) {
        AuthUserDTO requester = authService.getRequestUser(request);
        requester.requireId(userId).orElseThrow(() -> new ForbiddenException(requester));

        apiKeyService.deleteApiKey(
                apiKeyService.getApiKeyByName(
                        userService.getUserById(userId).orElseThrow(() -> new NotFoundException("User " + userId)), keyName
                ).orElseThrow(() -> new NotFoundException("ApiKey " + userId + "-" + keyName))
        );
    }

    private static class GoogleToken {
        private String googleToken;

        public String getGoogleToken() {
            return googleToken;
        }
    }

    private static class ApiKeyName {
        private String apikeyName;
        private String expiration;

        public String getApikeyName() {
            return apikeyName;
        }

        public Timestamp getExpiration() {
            return Timestamp.from(Instant.parse(expiration));
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
