package eu.mostserene.avogador.courseservice.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.courseservice.utils.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public UserDto getUser(UUID id) throws NotFoundException {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public UserDto getUser(String email) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    /**
     * Get the ID of a request
     *
     * @param request the current request
     * @return the ID as String
     */
    @Override
    public String getRequestID(HttpServletRequest request) {
        return request.getHeader("Request-ID");
    }
}
