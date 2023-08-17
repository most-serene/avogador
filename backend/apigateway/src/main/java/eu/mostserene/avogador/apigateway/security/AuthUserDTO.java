package eu.mostserene.avogador.apigateway.security;

import lombok.Data;

import java.util.UUID;

@Data
public class AuthUserDTO {
    private UUID id;
    private String email;
    private String givenName;
    private String familyName;
    private Boolean isProfessor;
    private Boolean isSuperuser;

    public AuthUserDTO() {
    }

    public AuthUserDTO(UUID id, String email, String givenName, String familyName, Boolean isProfessor, Boolean isSuperuser) {
        this.id = id;
        this.email = email;
        this.givenName = givenName;
        this.familyName = familyName;
        this.isProfessor = isProfessor;
        this.isSuperuser = isSuperuser;
    }
}
