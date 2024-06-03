package eu.mostserene.avogador.exerciseservice.usertrials;

import eu.mostserene.avogador.exerciseservice.practices.Practice;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(
        name = "UserTrials",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "trial_id"})
)
public class UserTrial {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @NotNull
    private UUID userId;

    @Setter
    @JoinColumn(name = "trial_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private Trial trial;

    @Setter
    private Date startTime;

    @Setter
    private Date finishTime;

    @Setter
    private Date deadline;

    @Setter
    @NotNull
    private Boolean hasExtraTime = false;

    public UserTrial() {
    }

    public UserTrial(UUID userId, Trial trial, Boolean hasExtraTime) {
        this.userId = userId;
        this.trial = trial;
        this.hasExtraTime = hasExtraTime;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public Trial getTrial() {
        return trial;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public Date getDeadline() {
        return deadline;
    }

    public Boolean getHasExtraTime() {
        return hasExtraTime;
    }

    public UserTrialDetailDto getUserTrialDetail(UserDto userDto) {
        return new UserTrialDetailDto(this, userDto);
    }

    public boolean isAfterDeadline() {
        return this.getTrial().isAfterDeadline(this.deadline);
    }

    public void init() {
        this.startTime = Date.from(Instant.now());
        if (trial instanceof Practice) {
            this.deadline = ((Practice) trial).getDeadline();
        }
        // TODO: add Exam
    }

}
