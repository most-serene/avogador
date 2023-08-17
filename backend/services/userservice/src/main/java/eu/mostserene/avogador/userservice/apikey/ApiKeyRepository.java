package eu.mostserene.avogador.userservice.apikey;

import eu.mostserene.avogador.userservice.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    Optional<ApiKey> findByUserAndName(@NonNull User user, @NonNull String name);
    List<ApiKey> findByUser(@NonNull User user);

    Optional<ApiKey> findByKeyHash(@NonNull String keyHash);

}
