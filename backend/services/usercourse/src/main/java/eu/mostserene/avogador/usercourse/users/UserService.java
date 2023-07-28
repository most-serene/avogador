package eu.mostserene.avogador.usercourse.users;

import eu.mostserene.avogador.usercourse.utils.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service

public interface UserService {
    UserDto getUser(Long id) throws NotFoundException;
    UserDto getUser(String email);
    UserDto getRequestUser(HttpServletRequest request);
}
