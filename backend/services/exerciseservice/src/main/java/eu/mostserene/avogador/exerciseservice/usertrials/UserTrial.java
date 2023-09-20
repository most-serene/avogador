package eu.mostserene.avogador.exerciseservice.usertrials;

import eu.mostserene.avogador.exerciseservice.trials.Trial;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(
        name = "UserTrials",
        uniqueConstraints = @UniqueConstraint(columnNames={"userId", "trial_id"})
)
public class UserTrial {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private UUID userId;

    @JoinColumn(name = "trial_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private Trial trial;

    private Date startTime;

    private Date finishTime;

    @NotNull
    private Date deadline;

    @NotNull
    private Boolean hasExtraTime = false;

    public UserTrial() {
    }

    public UserTrial(UUID userId, Trial trial, Date deadline, Boolean hasExtraTime) {
        this.userId = userId;
        this.trial = trial;
        this.deadline = deadline;
        this.hasExtraTime = hasExtraTime;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Trial getTrial() {
        return trial;
    }

    public void setTrial(Trial trial) {
        this.trial = trial;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Boolean getHasExtraTime() {
        return hasExtraTime;
    }

    public void setHasExtraTime(Boolean hasExtraTime) {
        this.hasExtraTime = hasExtraTime;
    }

    public UserTrialDetailDto getUserTrialDetail(UserDto userDto){
        return new UserTrialDetailDto(this, userDto);
    }

}
