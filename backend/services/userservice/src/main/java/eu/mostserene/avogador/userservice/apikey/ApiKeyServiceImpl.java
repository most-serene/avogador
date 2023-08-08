package eu.mostserene.avogador.userservice.apikey;

import com.google.common.hash.Hashing;
import eu.mostserene.avogador.userservice.users.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class ApiKeyServiceImpl implements ApiKeyService {

    @Autowired
    private ApiKeyRepository apiKeyRepository;


    @Override
    public Optional<ApiKey> getApiKey(Long apikeyId) {
        return apiKeyRepository.findById(apikeyId);
    }

    @Override
    public Optional<ApiKey> getApiKeyByName(User user, String name) {
        return apiKeyRepository.findByUserAndName(user, name);
    }

    @Override
    public List<ApiKey> getUserApiKey(User user) {
        return apiKeyRepository.findByUser(user);
    }

    @Override
    public String generateApiKey(User user, String name, Timestamp expiration) throws AlreadyExistingKeyException {
        if (apiKeyRepository.findByUserAndName(user, name).isPresent()) {
            throw new AlreadyExistingKeyException();
        }
        String key = RandomStringUtils.randomAlphanumeric(40);
        apiKeyRepository.save(new ApiKey(user, name, Hashing.sha256()
                .hashString(key, StandardCharsets.UTF_8)
                .toString(), expiration)
        );
        return key;
    }

    @Override
    public void deleteApiKey(ApiKey apiKey) {
        apiKeyRepository.delete(apiKey);
    }
}
