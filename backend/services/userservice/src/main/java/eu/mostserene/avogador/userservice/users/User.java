package eu.mostserene.avogador.userservice.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

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

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public Boolean getIsProfessor() {
        return isProfessor;
    }

    public void setIsProfessor(Boolean professor) {
        isProfessor = professor;
    }

    public Boolean getIsSuperuser() {
        return isSuperuser;
    }

    public void setIsSuperuser(Boolean superuser) {
        isSuperuser = superuser;
    }

    public Timestamp getJwtValidity() {
        return jwtValidity;
    }

    public void setJwtValidity(Timestamp jwtValidity) {
        this.jwtValidity = jwtValidity;
    }

    public AuthUserDTO generateAuthUserDTO() {
        return new AuthUserDTO(getId(), getEmail(), getGivenName(),
                getFamilyName(), getIsProfessor(), getIsSuperuser());
    }
}
