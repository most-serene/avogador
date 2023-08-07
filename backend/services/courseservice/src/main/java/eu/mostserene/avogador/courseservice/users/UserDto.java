package eu.mostserene.avogador.courseservice.users;

import eu.mostserene.avogador.courseservice.security.ForbiddenException;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String givenName;
    private String familyName;
    private Boolean isProfessor = false;
    private Boolean isSuperuser = false;

    public UserDto() {
    }

    public UserDto(Long id,  String email, String givenName, String familyName, Boolean isProfessor, Boolean isSuperuser) {
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
    public UserDto requireId(Long requiredId) {
        if (getId().equals(requiredId)) return this;
        throw new ForbiddenException(this);
    }

    /**
     * Ensures that the user is a professor
     * @throws ForbiddenException if the user is not a professor
     */
    public UserDto requireProfessor() {
        if (getIsProfessor()) return this;
        throw new ForbiddenException(this);
    }

    /**
     * Ensures that the user is a superuser
     * @throws ForbiddenException if the user is not a superuser
     */
    public UserDto requireSuperuser() {
        if (getIsSuperuser()) return this;
        throw new ForbiddenException(this);
    }
}
