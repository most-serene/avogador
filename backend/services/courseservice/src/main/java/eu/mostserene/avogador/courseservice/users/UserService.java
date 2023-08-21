package eu.mostserene.avogador.courseservice.users;

import eu.mostserene.avogador.courseservice.utils.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public interface UserService {
    UserDto getUser(UUID id) throws NotFoundException;
    UserDto getUser(String email);
    String getRequestID(HttpServletRequest request);
}
