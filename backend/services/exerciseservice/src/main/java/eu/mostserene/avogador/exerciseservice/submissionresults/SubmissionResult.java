package eu.mostserene.avogador.exerciseservice.submissionresults;

import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import eu.mostserene.avogador.exerciseservice.testcases.Testcase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(
        name = "SubmissionResults",
        uniqueConstraints = @UniqueConstraint(columnNames={"submission_id", "testcase_id"})
)
public class SubmissionResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "submission_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private Submission submission;

    @JoinColumn(name = "testcase_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private Testcase testcase;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SubmissionStatus status = SubmissionStatus.PENDING;

    public SubmissionResult() {
    }

    public SubmissionResult(Submission submission, Testcase testcase, SubmissionStatus status) {
        this.submission = submission;
        this.testcase = testcase;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public Testcase getTestcase() {
        return testcase;
    }

    public void setTestcase(Testcase testcase) {
        this.testcase = testcase;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public SubmissionResultDto toDto() {
        return new SubmissionResultDto(id, submission.getId(), testcase.getId(), status);
    }
}
