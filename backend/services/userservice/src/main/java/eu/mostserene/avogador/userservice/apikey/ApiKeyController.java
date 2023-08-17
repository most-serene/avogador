package eu.mostserene.avogador.userservice.apikey;

import eu.mostserene.avogador.userservice.security.ForbiddenException;
import eu.mostserene.avogador.userservice.users.AuthUserDTO;
import eu.mostserene.avogador.userservice.users.UserService;
import eu.mostserene.avogador.userservice.utils.BadRequestException;
import eu.mostserene.avogador.userservice.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/public/users/{userId}/api-key")
public class ApiKeyController {

    @Autowired
    private ApiKeyService apiKeyService;
    @Autowired
    private UserService userService;

    /**
     * Get all the API key owned by a user
     *
     * @param user    the current request user
     * @param userId  the id of the user who owns the keys
     * @return the list of the API keys
     */
    @GetMapping("")
    private List<ApiKeyDTO> getUserApiKeys(@RequestHeader(name = "User") AuthUserDTO user, @PathVariable UUID userId) {
        user.requireId(userId).orElseThrow(() -> new ForbiddenException(user));

        return apiKeyService.getApiKeyByUser(
                        userService.getUserById(userId).orElseThrow(() -> new NotFoundException(userId.toString())))
                .stream()
                .map(apiKey -> new ApiKeyDTO(apiKey.getId(), apiKey.getName(),
                        apiKey.getUser().getId(), apiKey.getCreationTimestamp(), apiKey.getExpirationTimestamp()))
                .toList();
    }

    /**
     * Generate an API key for the given user
     *
     * @param user       the current request user
     * @param userId     the id of the user who will own the key
     * @param apiKeyName the friendly name of the key
     * @return the generated key
     * @throws AlreadyExistingKeyException if the pair user-name already exists
     */
    @PostMapping("")
    private String generateApiKey(@RequestHeader(name = "User") AuthUserDTO user, @PathVariable UUID userId, @RequestBody ApiKeyName apiKeyName) {
        user.requireId(userId).orElseThrow(() -> new ForbiddenException(user));

        if (apiKeyName.getName().split("\\s+").length > 1) {
            throw new BadRequestException("ApiKey name cannot contain spaces");
        }

        return apiKeyService.generateApiKey(
                userService.getUserById(user.getId())
                        .orElseThrow(() -> new NotFoundException(userId.toString())),
                apiKeyName.getName(), apiKeyName.getExpiration()
        );
    }

    /**
     * Delete an API key
     *
     * @param user    the current request
     * @param userId  the id of the user who owns the key
     * @param keyName the friendly name of the key
     */
    @DeleteMapping("/{keyName}")
    private void deleteApiKey(@RequestHeader(name = "User") AuthUserDTO user, @PathVariable UUID userId, @PathVariable String keyName) {
        user.requireId(userId).orElseThrow(() -> new ForbiddenException(user));

        apiKeyService.deleteApiKey(
                apiKeyService.getApiKeyByName(
                        userService.getUserById(userId).orElseThrow(() -> new NotFoundException("User " + userId)), keyName
                ).orElseThrow(() -> new NotFoundException("ApiKey " + userId + "-" + keyName))
        );
    }

    private static class ApiKeyName {
        private String name;
        private String expiration;

        public String getName() {
            return name;
        }

        public Timestamp getExpiration() {
            return Timestamp.from(Instant.parse(expiration));
        }
    }
}
