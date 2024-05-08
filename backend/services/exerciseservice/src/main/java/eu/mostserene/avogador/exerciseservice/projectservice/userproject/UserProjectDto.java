package eu.mostserene.avogador.exerciseservice.projectservice.userproject;

import eu.mostserene.avogador.exerciseservice.users.UserDto;
import lombok.Data;

import java.util.UUID;

@Data
public class UserProjectDto {
    private UUID id;
    private UserDto user;
    private UUID projectId;
    private Integer mark;

    public UserProjectDto(UserProject userProject, UserDto user) {
        this.id = userProject.getId();
        this.user = user;
        this.projectId = userProject.getProject().getId();
        this.mark = userProject.getMark();
    }
}