package eu.mostserene.avogador.userservice.users;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(Long userId);
    Optional<User> getUserByEmail(String email);
    //User authenticateWithGoogle(String googleToken);
    User updateUser(User user);
    void deleteUser(User user);
}
