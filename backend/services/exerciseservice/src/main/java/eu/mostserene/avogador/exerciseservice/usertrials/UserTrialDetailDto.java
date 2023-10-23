package eu.mostserene.avogador.exerciseservice.usertrials;

import eu.mostserene.avogador.exerciseservice.users.UserDto;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class UserTrialDetailDto {
    private UUID id;
    private UserDto user;
    private UUID trialId;
    private Date startTime;
    private Date finishTime;
    private Date deadline;
    private Boolean hasExtraTime;


    public UserTrialDetailDto(UserTrial userTrial, UserDto user) {
        this.id = userTrial.getId();
        this.user = user;
        this.trialId = userTrial.getTrial().getId();
        this.startTime = userTrial.getStartTime();
        this.finishTime = userTrial.getFinishTime();
        this.deadline = userTrial.getDeadline();
        this.hasExtraTime = userTrial.getHasExtraTime();
    }
}
