package eu.mostserene.avogador.userservice.users;

import lombok.Data;

import java.util.Optional;

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
     * @return this if granted, empty otherwise
     */
    public Optional<AuthUserDTO> requireId(Long requiredId) {
        if (getId().equals(requiredId)) return Optional.of(this);
        return Optional.empty();
    }

    /**
     * Ensures that the user is a professor
     * @return this if granted, empty otherwise
     */
    public Optional<AuthUserDTO> requireProfessor() {
        if (getIsProfessor()) return Optional.of(this);
        return Optional.empty();
    }

    /**
     * Ensures that the user is a superuser
     * @return this if granted, empty otherwise
     */
    public Optional<AuthUserDTO> requireSuperuser() {
        if (getIsSuperuser())  return Optional.of(this);
        return Optional.empty();
    }
}
