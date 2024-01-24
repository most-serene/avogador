package eu.mostserene.avogador.storageservice.trials;

import eu.mostserene.avogador.storageservice.logger.AvogadorLogMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class TrialLogDto extends TrialDTO {
    AvogadorLogMessage avogadorLogMessage;

    public TrialLogDto() {
    }

    public TrialLogDto(UUID courseId, UUID trialId) {
        super(courseId, trialId);
    }
}
