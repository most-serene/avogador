package eu.mostserene.avogador.userservice.security;

import eu.mostserene.avogador.userservice.users.User;
import eu.mostserene.avogador.userservice.users.UserService;
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
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
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

        final Map<String, Object> claims = new HashMap<>();

        claims.put("user", user.generateAuthUserDTO());
        claims.put("generation_timestamp", Timestamp.from(Instant.now()));
        JwtBuilder builder = Jwts.builder()
                .setId(String.valueOf(user.getId()))
                .setIssuedAt(now)
                .setSubject(user.getEmail())
                .setIssuer("Avogador")
                .addClaims(claims)
                .signWith(signatureAlgorithm, signingKey);

        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        log.info(builder.toString());

        return builder.compact();
    }

    /*
    public User decodeJwt(String jwt) {
        try {
            return User.createFromCookie(String.valueOf(
                    Jwts.parser()
                            .setSigningKey(Base64.getEncoder().encode(jwtSecret.getBytes()))
                            .parseClaimsJws(jwt)
                            .getBody()
                            .get("user")).replace("=", ":"));
        } catch (ParseException e) {
            throw new MissingJwtException();
        }
    }*/

    private String extractJwt(HttpServletRequest request) {
        return Stream.of(request.getCookies() != null ? request.getCookies() : new Cookie[]{})
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .findFirst().orElseThrow(MissingJwtException::new).getValue();
    }

    public record GoogleUser(String email, String domain, String givenName, String familyName, String picture) { }

}
