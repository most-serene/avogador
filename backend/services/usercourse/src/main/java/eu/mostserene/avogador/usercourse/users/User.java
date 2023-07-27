package eu.mostserene.avogador.usercourse.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment")
    @Column(columnDefinition = "serial")
    private Long id;

    @NotNull
    private String email;

    @NotNull
    private String givenName;

    @NotNull
    private String familyName;

    @NotNull
    private Boolean isProfessor = false;

    @NotNull
    private Boolean isSuperuser = false;

    public User() {
    }

    public User(String email, String givenName, String familyName, Boolean isProfessor, Boolean isSuperuser) {
        this.email = email;
        this.givenName = givenName;
        this.familyName = familyName;
        this.isProfessor = isProfessor;
        this.isSuperuser = isSuperuser;
    }

    public Long getId() {
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
}
