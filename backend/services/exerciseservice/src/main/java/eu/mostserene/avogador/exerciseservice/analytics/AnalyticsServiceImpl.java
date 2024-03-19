package eu.mostserene.avogador.exerciseservice.analytics;

import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionResult;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionResultService;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionStatus;
import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import eu.mostserene.avogador.exerciseservice.submissions.SubmissionService;
import eu.mostserene.avogador.exerciseservice.trials.TrialService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private TrialService trialService;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private SubmissionResultService submissionResultService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UserCourseService userCourseService;


    @Override
    public Map<UUID, StudentTrialStatus> getStudentProgress(UUID userId, UUID courseId) {
        Map<UUID, StudentTrialStatus> statusMap = new HashMap<>();
        trialService.getTrialsByCourseId(courseId, false)
                .stream()
                .peek(trial -> statusMap.put(trial.getId(), new StudentTrialStatus(trial.getId(), trial.getName())))
                .flatMap(trial -> exerciseService.getExercisesFromTrial(trial, false).stream())
                .forEach(exercise -> {
                    Optional<Submission> lastSubmission = submissionService.getLatestSubmissionFromExerciseAndUserId(exercise, userId);
                    if (lastSubmission.isEmpty()) {
                        statusMap.get(exercise.getTrial().getId()).getMissing().incrementAndGet();
                        return;
                    }
                    List<SubmissionStatus> submissionResults = submissionResultService.getResultsFromSubmission(lastSubmission.get())
                            .stream()
                            .map(SubmissionResult::getStatus).toList();
                    if (submissionResults.isEmpty()) {
                        statusMap.get(exercise.getTrial().getId()).getMissing().incrementAndGet();
                    } else if (submissionResults.stream().allMatch(submissionStatus -> submissionStatus.equals(SubmissionStatus.CORRECT))) {
                        statusMap.get(exercise.getTrial().getId()).getPassed().incrementAndGet();
                    } else if (submissionResults.stream().anyMatch(submissionStatus ->
                            submissionStatus.equals(SubmissionStatus.WRONG_ANSWER) ||
                                    submissionStatus.equals(SubmissionStatus.COMPILE_ERROR) ||
                                    submissionStatus.equals(SubmissionStatus.RUNTIME_ERROR) ||
                                    submissionStatus.equals(SubmissionStatus.TIME_LIMIT_EXCEEDED)
                    )) {
                        statusMap.get(exercise.getTrial().getId()).getWrong().incrementAndGet();
                    } else {
                        statusMap.get(exercise.getTrial().getId()).getMissing().incrementAndGet();
                    }
                });

        return statusMap;
    }

    @Override
    public Map<SubmissionStatus, Long> getExerciseResults(Exercise exercise) {
        var results = submissionResultService.getResultsFromExercise(exercise);
        return Stream.of(SubmissionStatus.values())
                .filter(submissionStatus -> !submissionStatus.equals(SubmissionStatus.PENDING))
                .collect(Collectors.toMap(
                                Function.identity(),
                                status -> results.stream().filter(result -> result.getStatus() == status).count()
                        )
                );
    }

    @Override
    public List<Date> getSubmissionsTrend(UUID courseId) {
        return trialService.getTrialsByCourseId(courseId, true)
                .stream()
                .flatMap(trial -> exerciseService.getExercisesFromTrial(trial, true).stream())
                .flatMap(exercise -> submissionService.getSubmissionsFromExercise(exercise).stream())
                .map(Submission::getTimestamp)
                .toList();
    }
}
