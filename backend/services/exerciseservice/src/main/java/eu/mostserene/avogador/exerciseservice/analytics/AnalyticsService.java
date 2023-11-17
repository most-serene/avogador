package eu.mostserene.avogador.exerciseservice.analytics;

import java.util.Map;
import java.util.UUID;

public interface AnalyticsService {
    Map<UUID, StudentTrialStatus> getStudentProgress(UUID userId, UUID courseId);

    void getExerciseResults();

    void getSubmissionsTrend();
}
