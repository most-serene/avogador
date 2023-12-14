package eu.mostserene.avogador.userservice.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Email(message = "Please provide a valid email address")
    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    private String givenName;

    @NotNull
    private String familyName;

    @NotNull
    private Boolean isProfessor = false;

    @NotNull
    private Boolean isSuperuser = false;

    @NotNull
    private Timestamp jwtValidity = Timestamp.from(Instant.now());

    public User() {
    }

    public User(String email, String givenName, String familyName) {
        this.email = email;
        this.givenName = givenName;
        this.familyName = familyName;
        this.jwtValidity = Timestamp.from(Instant.now());
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setIsProfessor(Boolean professor) {
        isProfessor = professor;
    }

    public void setIsSuperuser(Boolean superuser) {
        isSuperuser = superuser;
    }

    public void setJwtValidity(Timestamp jwtValidity) {
        this.jwtValidity = jwtValidity;
    }

    public AuthUserDTO generateAuthUserDTO() {
        return new AuthUserDTO(getId(), getEmail(), getGivenName(),
                getFamilyName(), getIsProfessor(), getIsSuperuser());
    }
}
