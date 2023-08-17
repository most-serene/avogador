package eu.mostserene.avogador.userservice.users;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> getUserById(UUID userId);
    Optional<User> getUserByEmail(String email);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(User user);
}
