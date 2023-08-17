package eu.mostserene.avogador.userservice.apikey;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class ApiKeyDTO {
    private UUID id;
    private String name;
    private UUID userId;
    private Timestamp creationTimestamp;
    private Timestamp expirationTimestamp;

    public ApiKeyDTO() {
    }

    public ApiKeyDTO(UUID id, String name, UUID userId, Timestamp creationTimestamp, Timestamp expirationTimestamp) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.creationTimestamp = creationTimestamp;
        this.expirationTimestamp = expirationTimestamp;
    }
}
