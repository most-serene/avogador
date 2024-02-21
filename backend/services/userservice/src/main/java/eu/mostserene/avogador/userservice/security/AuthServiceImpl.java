package eu.mostserene.avogador.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import eu.mostserene.avogador.userservice.apikey.ApiKeyService;
import eu.mostserene.avogador.userservice.profilemanager.ExecutionProfile;
import eu.mostserene.avogador.userservice.users.AuthUserDTO;
import eu.mostserene.avogador.userservice.users.User;
import eu.mostserene.avogador.userservice.users.UserService;
import eu.mostserene.avogador.userservice.utils.LoggerColors;
import eu.mostserene.avogador.userservice.utils.NotFoundException;
import io.jsonwebtoken.*;
import io.sentry.Sentry;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    private ExecutionProfile executionProfile;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("#{'${customer.domains}'.split(',')}")
    private Set<String> customerDomains;

    @Override
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

            String domain = (String) response.get("hd");
            final String email = (String) response.get("email");
            final String givenName = (String) response.get("given_name");
            final String familyName = (String) response.get("family_name");
            final String picture = (String) response.get("picture");

            if (domain == null) {
                domain = email.split("@")[1];
            }

            GoogleUser googleUser = new GoogleUser(email, domain, givenName, familyName, picture);

            checkUserDomain(googleUser);

            return googleUser;
        } catch (IOException | ParseException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MicrosoftUser getMicrosoftUser(String microsoftToken) throws InvalidDomainException {
        log.info(LoggerColors.cyan(microsoftToken));
        try {
            HttpRequest httpRequest = HttpRequest
                    .newBuilder()
                    .uri(new URI("https://graph.microsoft.com/v1.0/me"))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + microsoftToken)
                    .GET()
                    .build();

            HttpResponse<String> content = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

            JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            final JSONObject response = (JSONObject) parser.parse(content.body());

            final String email = (String) response.get("mail");
            final String domain = email.split("@")[1];
            final String givenName = (String) response.get("givenName");
            final String familyName = (String) response.get("surname");

            MicrosoftUser microsoftUser = new MicrosoftUser(email, domain, givenName, familyName, null);
            checkUserDomain(microsoftUser);

            return microsoftUser;
        } catch (IOException | ParseException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkUserDomain(ThirdPartyAuthUser thirdPartyAuthUser) throws InvalidDomainException {
        log.info(LoggerColors.warn("Login attempt from " + thirdPartyAuthUser.getEmail() + " (" + thirdPartyAuthUser.getGivenName() + " " + thirdPartyAuthUser.getFamilyName() + ")"));

        if (!customerDomains.contains(thirdPartyAuthUser.getDomain())) {
            log.error(LoggerColors.error("Login denied to " + thirdPartyAuthUser.getEmail()));
            throw new InvalidDomainException();
        }

        log.info(LoggerColors.success("Login granted to " + thirdPartyAuthUser.getEmail()));
    }

    @Override
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
            Date exp = new Date(nowMillis + ttlMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    @Override
    public AuthUserDTO decodeJwt(String jwt) {
        final ObjectMapper mapper = new ObjectMapper();
        Claims jwsMap = null;

        try {
            jwsMap = Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encode(jwtSecret.getBytes()))
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (JwtException jwtException) {
            Sentry.captureException(jwtException);
            throw new ForbiddenException(jwtException.getMessage());
        }

        AuthUserDTO authUserDTO = mapper.convertValue(jwsMap.get("user"), AuthUserDTO.class);
        Timestamp generationTimestamp = mapper.convertValue(jwsMap.get("generation_timestamp"), Timestamp.class);

        User authUser = userService.getUserById(authUserDTO.getId())
                .orElseThrow(() -> new ForbiddenException("The token is revoked"));

        if (isJwtRevoked(authUser, generationTimestamp)) {
            throw new ForbiddenException("The token is revoked");
        }
        return authUser.generateAuthUserDTO();
    }

    /**
     * If the jwt creation timestamp is before the user jwtValidity timestamp, a ForbiddenException is thrown
     *
     * @param user                the user claimed by the jwt
     * @param generationTimestamp the timestamp included in the jwt
     */
    private boolean isJwtRevoked(User user, Timestamp generationTimestamp) {
        return user.getJwtValidity().compareTo(generationTimestamp) > 0;

    }

    @Override
    public String extractJwt(HttpServletRequest request) {
        return Stream.of(request.getCookies() != null ? request.getCookies() : new Cookie[]{})
                .filter(cookie -> executionProfile.getJWTKey().equals(cookie.getName()))
                .findFirst().orElseThrow(MissingJwtException::new).getValue();
    }

    @Override
    public String getRequestID(HttpServletRequest request) {
        return request.getHeader("Request-ID");
    }

    @Override
    public void revokeUserJWTs(UUID userId) {
        User user = userService.getUserById(userId).orElseThrow(() -> new NotFoundException("User " + userId));
        user.setJwtValidity(Timestamp.from(Instant.now()));
        userService.updateUser(user);
    }

    @Override
    public <T> T executeOnRole(AuthUserDTO user,
                               Function<AuthUserDTO, T> studentCallback,
                               Function<AuthUserDTO, T> professorCallback,
                               Function<AuthUserDTO, T> superUserCallback) {
        if (user.getIsSuperuser()) {
            return superUserCallback.apply(user);
        } else if (user.getIsProfessor()) {
            return professorCallback.apply(user);
        } else {
            return studentCallback.apply(user);
        }
    }

    @Override
    public AuthUserDTO validateApiKey(String apiKey) {
        return userService.getUserById(
                apiKeyService.getApiKeyByHash(Hashing.sha256()
                                .hashString(apiKey, StandardCharsets.UTF_8).toString())
                        .orElseThrow(() -> new ForbiddenException("API key not valid"))
                        .getUser().getId()
        ).orElseThrow(() -> new ForbiddenException("API key not valid")).generateAuthUserDTO();
    }

    @Override
    public String generateWebSocketToken(AuthUserDTO user) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] apiKeySecretBytes = Base64.getEncoder().encode(jwtSecret.getBytes());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        return Jwts.builder()
                .setId(String.valueOf(user.getId()) + "-websocket")
                .setIssuedAt(now)
                .setSubject(user.getEmail() + "-websocket")
                .setIssuer("Avogador")
                .claim("userId", user.getId())
                .claim("generation_timestamp", Timestamp.from(Instant.now()))
                .signWith(signatureAlgorithm, signingKey)
                .compact();
    }

    @Override
    public AuthUserDTO decodeWebSocketToken(String webSocketToken) {
        final ObjectMapper mapper = new ObjectMapper();
        Claims jwsMap = null;

        try {
            jwsMap = Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encode(jwtSecret.getBytes()))
                    .parseClaimsJws(webSocketToken)
                    .getBody();
        } catch (JwtException jwtException) {
            Sentry.captureException(jwtException);
            throw new ForbiddenException(jwtException.getMessage());
        }

        UUID userId = UUID.fromString(mapper.convertValue(jwsMap.get("userId"), String.class));
        Timestamp generationTimestamp = mapper.convertValue(jwsMap.get("generation_timestamp"), Timestamp.class);

        User authUser = userService.getUserById(userId)
                .orElseThrow(() -> new ForbiddenException("The token is revoked"));

        if (isJwtRevoked(authUser, generationTimestamp)) {
            throw new ForbiddenException("The token is revoked");
        }
        return authUser.generateAuthUserDTO();
    }

    /**
     * Class representing the GoogleUser returned by the call to the Google Auth API
     */
    public static class GoogleUser extends ThirdPartyAuthUser {
        public GoogleUser(String email, String domain, String givenName, String familyName, String picture) {
            super(email, domain, givenName, familyName, picture, "google");
        }
    }

    /**
     * Class representing the MicrosoftUser returned by the call to the Microsoft Auth API
     */
    public static class MicrosoftUser extends ThirdPartyAuthUser {
        public MicrosoftUser(String email, String domain, String givenName, String familyName, String picture) {
            super(email, domain, givenName, familyName, picture, "microsoft");
        }
    }

}
