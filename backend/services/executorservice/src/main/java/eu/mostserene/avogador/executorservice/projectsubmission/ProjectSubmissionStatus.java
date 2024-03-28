package eu.mostserene.avogador.executorservice.projectsubmission;

public enum ProjectSubmissionStatus {
    PENDING("PENDING"),
    SUCCESS("SUCCESS"),
    ERROR("ERROR"),
    CONFIRMED("CONFIRMED");

    public final String status;

    ProjectSubmissionStatus(String status) {
        this.status = status;
    }
}
