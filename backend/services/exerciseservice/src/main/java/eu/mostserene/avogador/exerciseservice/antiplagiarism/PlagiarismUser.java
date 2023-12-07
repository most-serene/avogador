package eu.mostserene.avogador.exerciseservice.antiplagiarism;

import eu.mostserene.avogador.exerciseservice.users.UserDto;
import lombok.Data;

import java.util.UUID;

@Data
public class PlagiarismUser {
    private UUID id;
    private String email;
    private String givenName;
    private String familyName;

    public PlagiarismUser() {
    }

    public PlagiarismUser(UUID id, String email, String givenName, String familyName) {
        this.id = id;
        this.email = email;
        this.givenName = givenName;
        this.familyName = familyName;
    }

    public static PlagiarismUser fromUserDto(UserDto user) {
        return new PlagiarismUser(user.getId(), user.getEmail(), user.getGivenName(), user.getFamilyName());
    }
}
