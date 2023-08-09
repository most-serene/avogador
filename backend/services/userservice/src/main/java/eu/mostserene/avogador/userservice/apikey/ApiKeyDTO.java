package eu.mostserene.avogador.userservice.apikey;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ApiKeyDTO {
    private Long id;
    private String name;
    private Long userId;
    private Timestamp creationTimestamp;
    private Timestamp expirationTimestamp;

    public ApiKeyDTO() {
    }

    public ApiKeyDTO(Long id, String name, Long userId, Timestamp creationTimestamp, Timestamp expirationTimestamp) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.creationTimestamp = creationTimestamp;
        this.expirationTimestamp = expirationTimestamp;
    }
}
