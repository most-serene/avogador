package eu.mostserene.avogador.userservice.apikey;

import eu.mostserene.avogador.userservice.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByUserAndName(@NonNull User user, @NonNull String name);
    List<ApiKey> findByUser(@NonNull User user);

    Optional<ApiKey> findByKeyHash(@NonNull String keyHash);

}
