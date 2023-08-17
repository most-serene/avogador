package eu.mostserene.avogador.userservice.apikey;

import eu.mostserene.avogador.userservice.users.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyService {

    Optional<ApiKey> getApiKey(UUID apikeyId);
    Optional<ApiKey> getApiKeyByName(User user, String name);
    List<ApiKey> getApiKeyByUser(User user);
    Optional<ApiKey> getApiKeyByHash(String hash);
    String generateApiKey(User user, String name, Timestamp expiration) throws AlreadyExistingKeyException;
    void deleteApiKey(ApiKey apiKey);
}
