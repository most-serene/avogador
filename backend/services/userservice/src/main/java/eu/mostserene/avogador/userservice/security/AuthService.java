package eu.mostserene.avogador.userservice.security;

import eu.mostserene.avogador.userservice.security.AuthServiceImpl.GoogleUser;
import eu.mostserene.avogador.userservice.security.AuthServiceImpl.MicrosoftUser;
import eu.mostserene.avogador.userservice.users.AuthUserDTO;
import eu.mostserene.avogador.userservice.users.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;
import java.util.function.Function;

public interface AuthService {
    GoogleUser getGoogleUser(String googleToken) throws InvalidDomainException;
    MicrosoftUser getMicrosoftUser(String microsoftToken) throws InvalidDomainException;
    String generateJWT(User user, long ttlMillis);

    /**
     * Given a token, returns the claimed user
     *
     * @param jwt the json web token expressed as string
     * @return the user claimed by the jwt
     * @throws ForbiddenException if the token has been revoked
     */
    AuthUserDTO decodeJwt(String jwt);

    /**
     * Extract the JWT Cookie from a request and return it as a string
     *
     * @param request the current HTTP request
     * @return the JWT as String
     * @throws MissingJwtException if no JWT cookie is present
     */
    String extractJwt(HttpServletRequest request);

    /**
     * Get the ID of a request
     *
     * @param request the current request
     * @return the ID as String
     */
    String getRequestID(HttpServletRequest request);

    /**
     * Revokes all the existing jwt of a user by setting at now the jwtValidity timestamp of the user
     *
     * @param userId the id of the user whose tokens have to be revoked
     */
    void revokeUserJWTs(UUID userId);

    /**
     * Executes the given callbacks based on the authorization scope of the request user
     *
     * @param user              the request userDto
     * @param studentCallback   the callback to execute if the user is a student
     * @param professorCallback the callback to execute if the user is a professor
     * @param superUserCallback the callback to execute if the user is a superuser
     * @param <T>               the type of the callbacks result
     * @return the result of the callback
     */
    <T> T executeOnRole(AuthUserDTO user,
                        Function<AuthUserDTO, T> studentCallback,
                        Function<AuthUserDTO, T> professorCallback,
                        Function<AuthUserDTO, T> superUserCallback);

    AuthUserDTO validateApiKey(String apiKey);

    String generateWebSocketToken(AuthUserDTO user);

    AuthUserDTO decodeWebSocketToken(String webSocketToken);
}
