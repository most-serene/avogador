package eu.mostserene.avogador.courseservice.users;

import eu.mostserene.avogador.courseservice.utils.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    // UserDto getUser(UUID id) throws NotFoundException;
    // UserDto getUser(String email);
    String getRequestID(HttpServletRequest request);
    List<UserDto> getUsersFromIdList(List<UUID> ids);
    List<UserDto> getUsersFromIdList(List<UUID> ids, Optional<Integer> limit, Optional<Integer> offset, Optional<String> orderBy, Optional<String> direction);
}
