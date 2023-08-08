package eu.mostserene.avogador.userservice.apikey;

import eu.mostserene.avogador.userservice.users.User;
import lombok.Data;

@Data
public class ApiKeyDTO {
    private Long id;
    private String name;
    private Long userId;

    public ApiKeyDTO() {
    }

    public ApiKeyDTO(Long id, String name, Long userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }
}
