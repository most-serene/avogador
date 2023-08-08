package eu.mostserene.avogador.userservice.apikey;

import eu.mostserene.avogador.userservice.users.User;

import java.util.List;
import java.util.Optional;

public interface ApiKeyService {

    Optional<ApiKey> getApiKey(Long apikeyId);
    Optional<ApiKey> getApiKeyByName(User user, String name);
    List<ApiKey> getUserApiKey(User user);
    String generateApiKey(User user, String name) throws AlreadyExistingKeyException;
    void deleteApiKey(ApiKey apiKey);
}
