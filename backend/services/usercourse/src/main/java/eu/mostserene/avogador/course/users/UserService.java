package eu.mostserene.avogador.course.users;

import eu.mostserene.avogador.course.utils.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service

public interface UserService {
    UserDto getUser(Long id) throws NotFoundException;
    UserDto getUser(String email);
    UserDto getRequestUser(HttpServletRequest request);
}
