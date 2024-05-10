package eu.mostserene.avogador.userservice.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

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

    @Setter
    @Email(message = "Please provide a valid email address")
    @NotNull
    @Column(unique = true)
    private String email;

    @Setter
    private String givenName;

    @Setter
    private String familyName;

    @Setter
    @NotNull
    private Boolean isProfessor = false;

    @Setter
    @NotNull
    private Boolean isSuperuser = false;

    @Setter
    @NotNull
    private Timestamp jwtValidity = Timestamp.from(Instant.now());

    public User() {
    }

    public User(String email) {
        this.email = email;
        this.jwtValidity = Timestamp.from(Instant.now());
    }

    public User(String email, String givenName, String familyName) {
        this.email = email;
        this.givenName = givenName;
        this.familyName = familyName;
        this.jwtValidity = Timestamp.from(Instant.now());
    }

    public AuthUserDTO generateAuthUserDTO() {
        return new AuthUserDTO(getId(), getEmail(), getGivenName(),
                getFamilyName(), getIsProfessor(), getIsSuperuser());
    }
}
