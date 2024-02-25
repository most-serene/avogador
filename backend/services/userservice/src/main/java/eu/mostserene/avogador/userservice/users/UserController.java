package eu.mostserene.avogador.userservice.users;

import com.google.common.hash.Hashing;
import eu.mostserene.avogador.userservice.mail.EmailService;
import eu.mostserene.avogador.userservice.profilemanager.ExecutionProfile;
import eu.mostserene.avogador.userservice.security.AuthService;
import eu.mostserene.avogador.userservice.security.ThirdPartyAuthUser;
import eu.mostserene.avogador.userservice.security.ForbiddenException;
import eu.mostserene.avogador.userservice.security.InvalidDomainException;
import eu.mostserene.avogador.userservice.security.restapicontrol.EnablePublicRestAPI;
import eu.mostserene.avogador.userservice.utils.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    private ExecutionProfile executionProfile;

    @GetMapping("")
    private List<AuthUserDTO> getUsers(@RequestHeader(name = "User") AuthUserDTO user) {
        if (!user.getIsSuperuser()) {
            throw new ForbiddenException(user);
        }
        return userService.getUsers().stream()
                .map(User::generateAuthUserDTO)
                .toList();
    }

    /**
     * Get the user claimed by the current JWT cookie
     *
     * @param request the current HTTP request
     * @return the related AuthUserDTO
     */
    @GetMapping("/current")
    private AuthUserDTO getCurrentUser(HttpServletRequest request) {
        return authService.decodeJwt(authService.extractJwt(request));
    }

    @GetMapping("/websocket-token")
    private String getWebSocketToken(@RequestHeader(name = "User") AuthUserDTO user) {
        return authService.generateWebSocketToken(user);
    }

    /**
     * Get a user by id, if called by a student, and it's not themselves, the email is obfuscated
     *
     * @param user   the current user from the header
     * @param userId the id of the user
     * @return the corresponding user
     */
    @GetMapping("/{userId}")
    @EnablePublicRestAPI
    private AuthUserDTO getUserById(@RequestHeader(name = "User") AuthUserDTO user, @PathVariable UUID userId) {
        var responseUser = userService.getUserById(userId)
                .orElseThrow(NotFoundException::new);

        if (!user.getIsProfessor() && !user.getIsSuperuser() && !user.getId().equals(userId))
            responseUser.setEmail(null);

        return responseUser.generateAuthUserDTO();
    }

    /**
     * Get a user by their email
     *
     * @param email the email of the user
     * @return the corresponding user
     */
    @GetMapping("/email/{email}")
    private AuthUserDTO getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User " + email))
                .generateAuthUserDTO();
    }

    /**
     * Delete a user by id. Only a superuser or the user itself are allowed to do that.
     *
     * @param userId the id of the user
     */
    @DeleteMapping("/{userId}")
    private void deleteUser(@RequestHeader(name = "User") AuthUserDTO user, @PathVariable UUID userId) {
        var userToDelete = userService.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId));

        user.requireSuperuser().ifPresentOrElse(
                superuser -> userService.deleteUser(userToDelete),
                () -> {
                    user.requireId(userId)
                            .orElseThrow(() -> new ForbiddenException(user));
                    userService.deleteUser(userToDelete);
                }
        );
    }

    @PutMapping("/professors/{userId}")
    private AuthUserDTO promoteUserToProfessor(@RequestHeader(name = "User") AuthUserDTO user, @PathVariable UUID userId) {
        if (!user.getIsSuperuser()) {
            throw new ForbiddenException(user);
        }

        var userToPromote = userService.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId));

        return userService
                .toProfessor(userToPromote)
                .generateAuthUserDTO();
    }

    @PutMapping("/students/{userId}")
    private AuthUserDTO demoteUserToStudent(@RequestHeader(name = "User") AuthUserDTO user, @PathVariable UUID userId) {
        if (!user.getIsSuperuser()) {
            throw new ForbiddenException(user);
        }

        var userToDemote = userService.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId));

        return userService
                .toStudent(userToDemote)
                .generateAuthUserDTO();
    }

    /**
     * Login using a Google token and retrieve the associated user
     *
     * @param response    the http response
     * @param googleToken the token generated by Google
     * @return the authUserDTO containing also the profile image URL and the hash of the JWT to be used against CSRF
     * @throws InvalidDomainException if the email domain is not in the customer's domains
     */
    @PostMapping("/google-auth")
    private AuthUserDTOImageHash authenticateWithGoogle(HttpServletResponse response, @RequestBody GoogleToken googleToken) throws InvalidDomainException {
        return authenticateUser(response,
                authService.getGoogleUser(googleToken.getGoogleToken())
        );
    }

    /**
     * Login using a Microsoft token and retrieve the associated user
     *
     * @param response       the http response
     * @param microsoftToken the token generated by Microsoft
     * @return the authUserDTO containing also the profile image URL and the hash of the JWT to be used against CSRF
     * @throws InvalidDomainException if the email domain is not in the customer's domains
     */
    @PostMapping("/microsoft-auth")
    private AuthUserDTOImageHash authenticateWithMicrosoft(HttpServletResponse response, @RequestBody MicrosoftToken microsoftToken) throws InvalidDomainException {
        return authenticateUser(response,
                authService.getMicrosoftUser(microsoftToken.getMicrosoftToken())
        );
    }

    private AuthUserDTOImageHash authenticateUser(HttpServletResponse response, ThirdPartyAuthUser thirdPartyAuthUser) {
        Optional<User> queriedUser = userService.getUserByEmail(thirdPartyAuthUser.getEmail());

        final User user = queriedUser.map(innerUser -> {
            if (innerUser.getGivenName().equals(thirdPartyAuthUser.getGivenName()) &&
                    innerUser.getFamilyName().equals(thirdPartyAuthUser.getFamilyName())) {
                return innerUser;
            }
            innerUser.setGivenName(thirdPartyAuthUser.getGivenName());
            innerUser.setFamilyName(thirdPartyAuthUser.getFamilyName());
            return userService.updateUser(innerUser);
        }).orElseGet(() -> {
            emailService.sendSimpleEmail(thirdPartyAuthUser.getEmail(), "Welcome to Avogador!",
                    "Hi " + thirdPartyAuthUser.getGivenName() + "!\nYou have been successfully registered to Avogador, enjoy!");

            return userService.createUser(new User(
                    thirdPartyAuthUser.getEmail(),
                    thirdPartyAuthUser.getGivenName(),
                    thirdPartyAuthUser.getFamilyName())
            );
        });

        String jwtContent = authService.generateJWT(user, TimeUnit.DAYS.toMillis(7) + TimeUnit.MINUTES.toMillis(1));
        ResponseCookie jwtCookie = executionProfile.buildJWT(user, jwtContent);

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        String jwtHash = Hashing.sha256()
                .hashString(jwtCookie.getValue(), StandardCharsets.UTF_8)
                .toString();

        return new AuthUserDTOImageHash(user.generateAuthUserDTO(), thirdPartyAuthUser.getPicture(),
                jwtHash.substring(jwtHash.length() - 20));

    }

    /**
     * Logout from the application
     *
     * @param response the http response
     */
    @GetMapping("/logout")
    private void logoutUser(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, executionProfile.logout().toString());
    }


    /**
     * Revoke all the existing JWTs for the user.
     * This call is allowed only from a superuser or the user itself
     *
     * @param user   the request user
     * @param userId the id of the user whose tokens have to be revoked
     */
    @PatchMapping("/{userId}/revoke-jwt")
    private void revokeJWTs(@RequestHeader(name = "User") AuthUserDTO user, @PathVariable UUID userId) {
        if (!user.getIsSuperuser() && !Objects.equals(user.getId(), userId)) {
            throw new ForbiddenException(user);
        }
        authService.revokeUserJWTs(userId);
    }


    @Getter
    private static class GoogleToken {
        private String googleToken;
    }

    @Getter
    private static class MicrosoftToken {
        private String microsoftUserId;
        private String microsoftToken;
    }


    @Setter
    @Getter
    private static class AuthUserDTOImageHash extends AuthUserDTO {
        private String picture;

        private String hash;


        public AuthUserDTOImageHash(UUID id, String email, String givenName, String familyName,
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

    }
}
