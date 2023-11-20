package eu.mostserene.avogador.storageservice.trials;

import lombok.Data;

import java.util.UUID;

@Data
public class TrialDTO {
    private UUID courseId;
    private UUID trialId;

    public TrialDTO() {
    }

    public TrialDTO(UUID courseId, UUID trialId) {
        this.courseId = courseId;
        this.trialId = trialId;
    }
}
