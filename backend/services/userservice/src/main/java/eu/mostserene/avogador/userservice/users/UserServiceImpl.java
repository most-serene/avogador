package eu.mostserene.avogador.userservice.users;

import eu.mostserene.avogador.userservice.utils.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository repository;

    @Override
    public List<User> getUsers() {
        return repository.findAll();
    }

    @Override
    public Optional<User> getUserById(UUID userId) {
        return repository.findById(userId);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    @Transactional
    public User createUser(User user) {
        return repository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        return repository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(User user) {
        repository.delete(user);
    }

    @Override
    public List<User> getUsersByIds(List<UUID> ids, Pageable sort) {
        return repository.findByIdIn(ids, sort);
    }

    @Override
    public User toProfessor(User user) {
        user.setIsProfessor(true);
        user.setJwtValidity(Timestamp.from(Instant.now()));

        return repository.save(user);
    }

    @Override
    public User toStudent(User user) {
        user.setIsProfessor(false);
        user.setJwtValidity(Timestamp.from(Instant.now()));

        return repository.save(user);
    }
}
