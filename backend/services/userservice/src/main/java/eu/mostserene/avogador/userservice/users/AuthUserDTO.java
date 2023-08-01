package eu.mostserene.avogador.userservice.users;

import lombok.Data;

@Data
public class AuthUserDTO {
    private Long id;
    private String email;
    private String givenName;
    private String familyName;
    private Boolean isProfessor;
    private Boolean isSuperuser;

    public AuthUserDTO(Long id, String email, String givenName, String familyName, Boolean isProfessor, Boolean isSuperuser) {
        this.id = id;
        this.email = email;
        this.givenName = givenName;
        this.familyName = familyName;
        this.isProfessor = isProfessor;
        this.isSuperuser = isSuperuser;
    }

    /*

    public User generateUser(String cookie) throws ParseException {
        JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        final JSONObject jsonUser = (JSONObject) parser.parse(cookie);

        final User user = new User();

        user. = Long.parseLong(String.valueOf(jsonUser.get("id")));



        return user;
    }*/

}
