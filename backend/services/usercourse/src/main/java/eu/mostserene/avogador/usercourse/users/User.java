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
    private Boolean isProfessor;

    @NotNull
    private Boolean isSuperuser;

    public User() {
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

    public Boolean getProfessor() {
        return isProfessor;
    }

    public void setProfessor(Boolean professor) {
        isProfessor = professor;
    }

    public Boolean getSuperuser() {
        return isSuperuser;
    }

    public void setSuperuser(Boolean superuser) {
        isSuperuser = superuser;
    }
}
