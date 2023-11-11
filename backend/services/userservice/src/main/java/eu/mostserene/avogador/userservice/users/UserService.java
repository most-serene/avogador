package eu.mostserene.avogador.userservice.users;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    List<User> getUsers();
    Optional<User> getUserById(UUID userId);
    Optional<User> getUserByEmail(String email);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(User user);
    List<User> getUsersByIds(List<UUID> ids, Pageable sort);
}
