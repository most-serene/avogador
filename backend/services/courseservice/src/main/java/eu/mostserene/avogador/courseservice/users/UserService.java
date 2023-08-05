package eu.mostserene.avogador.courseservice.users;

import eu.mostserene.avogador.courseservice.utils.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    UserDto getUser(Long id) throws NotFoundException;
    UserDto getUser(String email);
    UserDto getRequestUser(HttpServletRequest request);
    String getRequestID(HttpServletRequest request);
}
