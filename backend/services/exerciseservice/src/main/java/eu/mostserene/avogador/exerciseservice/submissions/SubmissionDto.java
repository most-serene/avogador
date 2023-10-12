package eu.mostserene.avogador.exerciseservice.submissions;

import eu.mostserene.avogador.exerciseservice.strox.StroxCell;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class SubmissionDto {
    private UUID id;
    private UUID exerciseId;
    private UUID userId;
    private Date timestamp;
    private List<StroxCell> stroxCells;

    public SubmissionDto() {
    }

    public SubmissionDto(UUID id, UUID exerciseId, UUID userId, Date timestamp, List<StroxCell> stroxCells) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.stroxCells = stroxCells;
    }
}
