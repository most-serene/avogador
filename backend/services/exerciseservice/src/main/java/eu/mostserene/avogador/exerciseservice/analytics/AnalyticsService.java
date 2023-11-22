package eu.mostserene.avogador.exerciseservice.analytics;

import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionStatus;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AnalyticsService {
    Map<UUID, StudentTrialStatus> getStudentProgress(UUID userId, UUID courseId);

    Map<SubmissionStatus, Long> getExerciseResults(Exercise exercise);

    List<Date> getSubmissionsTrend(UUID courseId);
}
