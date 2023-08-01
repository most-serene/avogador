package eu.mostserene.avogador.userservice.users;

import lombok.Data;

@Data
public class AuthUserDTO {
    private Long id;
    private String email;
    private String givenName;
    private String familyName;
    private Boolean isProfessor;
    private Boolean isSuperuser;

    public AuthUserDTO() {
    }

    public AuthUserDTO(Long id, String email, String givenName, String familyName, Boolean isProfessor, Boolean isSuperuser) {
        this.id = id;
        this.email = email;
        this.givenName = givenName;
        this.familyName = familyName;
        this.isProfessor = isProfessor;
        this.isSuperuser = isSuperuser;
    }
}
