package eu.mostserene.avogador.courseservice.users;

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
