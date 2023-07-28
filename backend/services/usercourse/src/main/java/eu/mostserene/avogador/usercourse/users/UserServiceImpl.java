package eu.mostserene.avogador.usercourse.users;

import eu.mostserene.avogador.usercourse.utils.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public UserDto getUser(Long id) throws NotFoundException {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public UserDto getUser(String email) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public UserDto getRequestUser(HttpServletRequest request) {
        throw new UnsupportedOperationException("Method not implemented");
    }
}
