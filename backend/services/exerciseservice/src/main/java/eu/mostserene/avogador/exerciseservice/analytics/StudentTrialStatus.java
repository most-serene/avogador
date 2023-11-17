package eu.mostserene.avogador.exerciseservice.analytics;

import lombok.Data;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class StudentTrialStatus {
    private UUID trialId;
    private String name;
    private AtomicInteger passed;
    private AtomicInteger wrong;
    private AtomicInteger missing;

    public StudentTrialStatus() {
    }

    StudentTrialStatus(UUID trialId, String name) {
        this.trialId = trialId;
        this.name = name;
        passed = new AtomicInteger(0);
        wrong = new AtomicInteger(0);
        missing = new AtomicInteger(0);
    }
}
