package eu.mostserene.avogador.userservice.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.userservice.users.AuthUserDTO;
import eu.mostserene.avogador.userservice.users.User;
import eu.mostserene.avogador.userservice.users.UserService;
import eu.mostserene.avogador.userservice.utils.NotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.Key;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
@Slf4j
public class AuthService {

    @Autowired
    private UserService userService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public GoogleUser getGoogleUser(String googleToken) throws InvalidDomainException {
        try {
            HttpRequest httpRequest = HttpRequest
                    .newBuilder()
                    .uri(new URI("https://oauth2.googleapis.com/tokeninfo?id_token=" + googleToken))
                    .GET()
                    .build();

            HttpResponse<String> content = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

            JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            final JSONObject response = (JSONObject) parser.parse(content.body());

            final String domain = (String) response.get("hd");
            final String email = (String) response.get("email");
            final String givenName = (String) response.get("given_name");
            final String familyName = (String) response.get("family_name");
            final String picture = (String) response.get("picture");

            // log.info(LoggerColors.warn("Login attempt from " + email + " (" + name + ")"));

            if ("unive.it".equals(domain) || "stud.unive.it".equals(domain)) {
                // log.info(LoggerColors.success("Login granted to " + email));
                return new GoogleUser(email, domain, givenName, familyName, picture);
            } else {
                // log.error(LoggerColors.error("Login denied to " + email));
                throw new InvalidDomainException();
            }
        } catch (IOException | ParseException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public String generateJWT(User user, long ttlMillis) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] apiKeySecretBytes = Base64.getEncoder().encode(jwtSecret.getBytes());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder()
                .setId(String.valueOf(user.getId()))
                .setIssuedAt(now)
                .setSubject(user.getEmail())
                .setIssuer("Avogador")
                .claim("user", user.generateAuthUserDTO())
                .claim("generation_timestamp", Timestamp.from(Instant.now()))
                .signWith(signatureAlgorithm, signingKey);

        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        log.info(builder.toString());

        return builder.compact();
    }


    /**
     * Given a token, returns the claimed user
     * @param jwt the json web token expressed as string
     * @return the user claimed by the jwt
     * @throws ForbiddenException if the token has been revoked
     */
    public AuthUserDTO decodeJwt(String jwt) {
        final ObjectMapper mapper = new ObjectMapper();

        Claims jwsMap = Jwts.parser()
                .setSigningKey(Base64.getEncoder().encode(jwtSecret.getBytes()))
                .parseClaimsJws(jwt)
                .getBody();

        AuthUserDTO authUserDTO = mapper.convertValue(jwsMap.get("user"), AuthUserDTO.class);
        Timestamp generationTimestamp = mapper.convertValue(jwsMap.get("generation_timestamp"), Timestamp.class);
        checkIfRevoked(authUserDTO, generationTimestamp);
        return authUserDTO;
    }

    /**
     * If the jwt creation timestamp is before the user jwtValidity timestamp, a ForbiddenException is thrown
     * @param authUserDTO the user claimed by the jwt
     * @param generationTimestamp the timestamp included in the jwt
     * @throws ForbiddenException thrown if the jwt has been revoked
     * @throws NotFoundException if the user has been deleted previously
     */
    private void checkIfRevoked(AuthUserDTO authUserDTO, Timestamp generationTimestamp) {
        if (userService.getUserById(authUserDTO.getId())
                .orElseThrow(() -> new ForbiddenException("The token is revoked"))
                .getJwtValidity().compareTo(generationTimestamp) > 0) {
            throw new ForbiddenException("The token is revoked");
        }
    }

    private String extractJwt(HttpServletRequest request) {
        return Stream.of(request.getCookies() != null ? request.getCookies() : new Cookie[]{})
                .filter(cookie -> "__Secure-jwt".equals(cookie.getName()))
                .findFirst().orElseThrow(MissingJwtException::new).getValue();
    }

    /**
     * Get the request associated to the current request
     * @param request the current request
     * @return the corresponding user
     */
    public AuthUserDTO getRequestUser(HttpServletRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(request.getHeader("User"), AuthUserDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the ID of a request
     * @param request the current request
     * @return the ID as String
     */
    public String getRequestID(HttpServletRequest request) {
        return request.getHeader("Request-ID");
    }

    /**
     * Revokes all the existing jwt of a user by setting at now the jwtValidity timestamp of the user
     * @param userId the id of the user whose tokens have to be revoked
     */
    public void revokeUserJWTs(Long userId) {
        User user = userService.getUserById(userId).orElseThrow(() -> new NotFoundException("User " + userId));
        user.setJwtValidity(Timestamp.from(Instant.now()));
        userService.updateUser(user);
    }

    /**
     * Executes the given callbacks based on the authorization scope of the request user
     * @param request the request
     * @param studentCallback the callback to execute if the user is a student
     * @param professorCallback the callback to execute if the user is a professor
     * @param superUserCallback the callback to execute if the user is a superuser
     * @return the result of the callback
     * @param <T> the type of the callbacks result
     */
    public <T> T executeOnRole(HttpServletRequest request,
                               Function<AuthUserDTO, T> studentCallback,
                               Function<AuthUserDTO, T> professorCallback,
                               Function<AuthUserDTO, T> superUserCallback) {
        AuthUserDTO user = getRequestUser(request);
        if (user.getIsSuperuser()) {
            return superUserCallback.apply(user);
        } else if (user.getIsProfessor()) {
            return professorCallback.apply(user);
        } else {
            return studentCallback.apply(user);
        }
    }

    /**
     * Record representing the GoogleUser returned by the call to the Google Auth API
     * @param email
     * @param domain
     * @param givenName
     * @param familyName
     * @param picture
     */
    public record GoogleUser(String email, String domain, String givenName, String familyName, String picture) { }

}
