package eu.mostserene.avogador.exerciseservice.antiplagiarism;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubmissionComparisonDetail extends SubmissionComparison {
    private List<Match> matches;

    public SubmissionComparisonDetail() {
    }

    public SubmissionComparisonDetail(UUID first, UUID second, float similarity, List<Match> matches) {
        super(first, second, similarity);
        this.matches = matches;
    }
}
