package eu.mostserene.avogador.userservice.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment")
    @Column(columnDefinition = "serial")
    private Long id;

    @Email(message = "Please provide a valid email address")
    @Pattern(regexp=".+@(stud\\.)?unive\\.it", message="Please provide a valid email address")
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

    public User() {
    }

    public User(String email, String givenName, String familyName) {
        this.email = email;
        this.givenName = givenName;
        this.familyName = familyName;
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
