package eu.mostserene.avogador.courseservice.users;

import lombok.Data;

import java.util.Optional;
import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String email;
    private String givenName;
    private String familyName;
    private Boolean isProfessor = false;
    private Boolean isSuperuser = false;

    public UserDto() {
    }

    public UserDto(UUID id,  String email, String givenName, String familyName, Boolean isProfessor, Boolean isSuperuser) {
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
    public Optional<UserDto> requireId(UUID requiredId) {
        if (getId().equals(requiredId)) return Optional.of(this);
        return Optional.empty();
    }

    /**
     * Ensures that the user is a professor
     * @return this if granted, empty otherwise
     */
    public Optional<UserDto> requireProfessor() {
        if (getIsProfessor()) return Optional.of(this);
        return Optional.empty();
    }

    /**
     * Ensures that the user is a superuser
     * @return this if granted, empty otherwise
     */
    public Optional<UserDto> requireSuperuser() {
        if (getIsSuperuser())  return Optional.of(this);
        return Optional.empty();
    }
}
