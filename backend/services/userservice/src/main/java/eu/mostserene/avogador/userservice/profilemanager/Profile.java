package eu.mostserene.avogador.userservice.profilemanager;

import eu.mostserene.avogador.userservice.users.User;
import org.springframework.http.ResponseCookie;

public interface Profile {
    String getJWTKey();
    ResponseCookie buildJWT(User user, String value);
    ResponseCookie logout();
}
