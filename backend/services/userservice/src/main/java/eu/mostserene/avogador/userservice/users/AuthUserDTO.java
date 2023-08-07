package eu.mostserene.avogador.userservice.users;

import eu.mostserene.avogador.userservice.security.ForbiddenException;
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

    /**
     * Ensures that the user has the give id
     * @param requiredId the required id
     * @throws ForbiddenException if the user has not the required id
     */
    public AuthUserDTO requireId(Long requiredId) {
        if (getId().equals(requiredId)) return this;
        throw new ForbiddenException(this);
    }

    /**
     * Ensures that the user is a professor
     * @throws ForbiddenException if the user is not a professor
     */
    public AuthUserDTO requireProfessor() {
        if (getIsProfessor()) return this;
        throw new ForbiddenException(this);
    }

    /**
     * Ensures that the user is a superuser
     * @throws ForbiddenException if the user is not a superuser
     */
    public AuthUserDTO requireSuperuser() {
        if (getIsSuperuser()) return this;
        throw new ForbiddenException(this);
    }
}
