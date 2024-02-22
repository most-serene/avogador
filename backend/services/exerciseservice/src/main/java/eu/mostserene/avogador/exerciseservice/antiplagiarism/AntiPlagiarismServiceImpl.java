package eu.mostserene.avogador.exerciseservice.antiplagiarism;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.Language;
import de.jplag.clustering.ClusteringOptions;
import de.jplag.exceptions.BasecodeException;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;
import eu.mostserene.avogador.exerciseservice.antiplagiarism.similarityreport.SimilarityReport;
import eu.mostserene.avogador.exerciseservice.antiplagiarism.similarityreport.SimilarityReportRepository;
import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import eu.mostserene.avogador.exerciseservice.strox.Strox;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionResultService;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionStatus;
import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import eu.mostserene.avogador.exerciseservice.submissions.SubmissionService;
import eu.mostserene.avogador.exerciseservice.users.UserService;
import eu.mostserene.avogador.exerciseservice.utils.LoggerColors;
import io.sentry.Sentry;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class AntiPlagiarismServiceImpl implements AntiPlagiarismService {
    @Autowired
    private StorageService storageService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private SubmissionResultService submissionResultService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimilarityReportRepository similarityReportRepository;

    @Override
    public void executeSimilarityTool(Exercise exercise) {
        log.debug(LoggerColors.cyan("Similarity Job started on exercise - " + exercise.getId()));
        try {
            similarityCheckJob(exercise);
            log.debug(LoggerColors.success("Similarity Job done on exercise - " + exercise.getId()));

            similarityReportRepository.save(
                    similarityReportRepository.findFirstByExercise_Id(exercise.getId())
                            .map(similarityReport -> {
                                similarityReport.setTimestamp(Date.from(Instant.now()));
                                return similarityReport;
                            })
                            .orElseGet(() -> new SimilarityReport(exercise, Date.from(Instant.now())))
            );
        } catch (Exception e) {
            log.error(LoggerColors.error("Similarity Job failed on exercise - " + exercise.getId()));
            log.error(LoggerColors.error(e.toString()));
            Sentry.captureException(e);
        }
    }

    @Override
    public Optional<SimilarityReport> getSimilarityReport(Exercise exercise) {
        return similarityReportRepository.findFirstByExercise_Id(exercise.getId());
    }

    @Override
    public Optional<PlagiarismReport> retrieveSimilarityReportFile(Exercise exercise) {
        return storageService.getSimilarityReport(exercise);
    }

    private void similarityCheckJob(Exercise exercise) throws IOException {
        File workingDirectory = Files.createTempDirectory("avogador").toFile();
        try {
            File baseCode = getAndUnpackTemplate(exercise, workingDirectory);

            Map<UUID, PlagiarismUser> submissions = getSubmissionsForCheck(exercise);
            File submissionsDirectories = getAndUnpackSubmissions(exercise, submissions.keySet().stream().toList(), workingDirectory);

            Language language = getLanguage(exercise);
            JPlagOptions options = new JPlagOptions(language,
                    Set.of(submissionsDirectories),
                    Set.of())
                    .withBaseCodeSubmissionDirectory(baseCode)
                    .withClusteringOptions(new ClusteringOptions().withEnabled(true));

            handleToolRun(options, exercise, submissions, workingDirectory);
            storageService.uploadSimilarityReport(exercise, new File(workingDirectory + "/results/report.json"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.deleteDirectory(workingDirectory);
        }
    }

    private File getAndUnpackTemplate(Exercise exercise, File workingDirectory) throws Exception {
        if (!(new File(workingDirectory.getPath() + "/template")).mkdirs()) {
            log.warn(LoggerColors.warn(workingDirectory.getPath() + "/template not created"));
        }

        Strox template = storageService.getExerciseTemplate(exercise)
                .orElseThrow(() -> new Exception("Exercise - " + exercise.getId() + " template not found"));

        File baseCode = (new File(workingDirectory.getPath() + "/template/" + template.getSourceFileName()));
        FileUtils.writeStringToFile(baseCode, Strox.generateSourceCode(template), Charset.defaultCharset());
        return baseCode;
    }

    private Language getLanguage(Exercise exercise) {
        return switch (exercise.getTrial().getLanguage()) {
            case C, CPP -> new de.jplag.cpp2.CPPLanguage();
            case PYTHON -> new de.jplag.python3.Language();
            case JAVA -> new de.jplag.java.Language();
        };
    }

    private void handleToolRun(JPlagOptions options, Exercise exercise, Map<UUID, PlagiarismUser> submissions, File workingDirectory) throws ExitException {
        try {
            runTool(options, exercise, submissions, workingDirectory);
        } catch (BasecodeException basecodeException) {
            runTool(new JPlagOptions(options.language(), options.submissionDirectories(), Set.of())
                            .withClusteringOptions(new ClusteringOptions().withEnabled(true)),
                    exercise, submissions, workingDirectory);
        }
    }

    private File getAndUnpackSubmissions(Exercise exercise, List<UUID> submissionIds, File workingDirectory) throws IOException {
        Resource submissions = storageService.getExerciseLatestSubmissionsSources(exercise, submissionIds);
        File submissionsArchive = new File(workingDirectory + "/submissions.tar.gz");
        FileUtils.copyInputStreamToFile(submissions.getInputStream(), submissionsArchive);
        File submissionsDirectory = new File(workingDirectory + "/submissions");
        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
        archiver.extract(submissionsArchive, submissionsDirectory);
        return submissionsDirectory;
    }

    private void runTool(JPlagOptions options, Exercise exercise, Map<UUID, PlagiarismUser> submissions, File workingDirectory) throws ExitException {
        JPlagResult result = (new JPlag(options)).run();
        PlagiarismReport report = generateReport(result, exercise, submissions);
        ObjectMapper mapper = new ObjectMapper();
        try {
            FileUtils.writeStringToFile(new File(workingDirectory + "/results/report.json"),
                    mapper.writeValueAsString(report),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PlagiarismReport generateReport(JPlagResult result, Exercise exercise, Map<UUID, PlagiarismUser> submissions) {
        PlagiarismReport report = new PlagiarismReport();

        report.setExerciseId(exercise.getId());
        report.setExecutionDate(new Date());
        report.setSubmissions(submissions);
        report.setComparisons(generateComparisons(result));
        report.setAverageMetrics(generateAverageMetric(result));
        report.setMaxMetrics(generateMaxMetric(result));
        report.setClusters(generateClusters(result));

        return report;
    }

    private Map<UUID, Map<UUID, SubmissionComparisonDetail>> generateComparisons(JPlagResult result) {
        Map<UUID, Map<UUID, SubmissionComparisonDetail>> comparisons = new HashMap<>();

        result.getAllComparisons()
                .stream()
                .filter(jPlagComparison -> jPlagComparison.maximalSimilarity() > 0)
                .forEach(jPlagComparison -> {
                    SubmissionComparisonDetail comparisonDetail = new SubmissionComparisonDetail();
                    comparisonDetail.setFirst(UUID.fromString(jPlagComparison.firstSubmission().getName()));
                    comparisonDetail.setSecond(UUID.fromString(jPlagComparison.secondSubmission().getName()));
                    comparisonDetail.setSimilarity(jPlagComparison.similarity());
                    var firstTokenList = jPlagComparison.firstSubmission().getTokenList();
                    var secondTokenList = jPlagComparison.secondSubmission().getTokenList();

                    comparisonDetail.setMatches(jPlagComparison.matches().stream()
                            .map(jPlagMatch -> {
                                Match match = new Match();

                                String firstPath = firstTokenList.get(
                                                jPlagMatch.startOfFirst())
                                        .getFile().toString();

                                String secondPath = secondTokenList.get(
                                                jPlagMatch.startOfSecond())
                                        .getFile().toString();

                                int secondIndex = secondPath.indexOf(jPlagComparison.secondSubmission().getName());

                                int firstIndex = firstPath.indexOf(jPlagComparison.firstSubmission().getName());

                                match.setFirstFile(
                                        firstPath.substring(firstIndex +
                                                jPlagComparison.firstSubmission().getName().length() + 1)

                                );
                                match.setSecondFile(
                                        secondPath.substring(secondIndex +
                                                jPlagComparison.secondSubmission().getName().length() + 1)
                                );

                                match.setFirstStart(
                                        firstTokenList.get(jPlagMatch.startOfFirst())
                                                .getLine()
                                );

                                match.setFirstEnd(
                                        firstTokenList.get(jPlagMatch.endOfFirst())
                                                .getLine()
                                );

                                match.setSecondStart(
                                        secondTokenList.get(jPlagMatch.startOfSecond())
                                                .getLine()
                                );
                                match.setSecondEnd(
                                        secondTokenList.get(jPlagMatch.endOfSecond())
                                                .getLine()
                                );

                                match.setTokens(jPlagMatch.length());

                                return match;
                            }).toList());

                    comparisons.putIfAbsent(comparisonDetail.getFirst(), new HashMap<>());
                    comparisons.get(comparisonDetail.getFirst())
                            .put(comparisonDetail.getSecond(), comparisonDetail);

                });

        return comparisons;
    }

    private Metric generateAverageMetric(JPlagResult result) {
        Metric averageMetric = new Metric();
        averageMetric.setDistribution(Arrays.stream(result.getSimilarityDistribution()).boxed().toList());
        averageMetric.setTopComparison(
                result.getAllComparisons()
                        .stream()
                        .filter(jPlagComparison -> jPlagComparison.similarity() > 0)
                        .sorted((a, b) -> Double.compare(b.similarity(), a.similarity()))
                        .map(jPlagComparison -> new SubmissionComparison(
                                UUID.fromString(jPlagComparison.firstSubmission().getName()),
                                UUID.fromString(jPlagComparison.secondSubmission().getName()),
                                jPlagComparison.similarity()))
                        .limit(100)
                        .toList()
        );
        return averageMetric;
    }


    private Metric generateMaxMetric(JPlagResult result) {
        Metric maxMetric = new Metric();
        maxMetric.setDistribution(Arrays.stream(result.getMaxSimilarityDistribution()).boxed().toList());
        maxMetric.setTopComparison(
                result.getAllComparisons()
                        .stream()
                        .filter(jPlagComparison -> jPlagComparison.maximalSimilarity() > 0)
                        .sorted((a, b) -> Double.compare(b.maximalSimilarity(), a.maximalSimilarity()))
                        .map(jPlagComparison -> new SubmissionComparison(
                                UUID.fromString(jPlagComparison.firstSubmission().getName()),
                                UUID.fromString(jPlagComparison.secondSubmission().getName()),
                                jPlagComparison.maximalSimilarity()))
                        .limit(100)
                        .toList()
        );
        return maxMetric;
    }


    private List<Cluster> generateClusters(JPlagResult result) {

        return result.getClusteringResult().stream()
                .flatMap(submissionClusteringResult -> submissionClusteringResult.getClusters().stream())
                .map(submissionCluster -> {
                    Cluster cluster = new Cluster();

                    cluster.setStrength(submissionCluster.getCommunityStrength());
                    cluster.setMembers(submissionCluster.getMembers().stream()
                            .map(de.jplag.Submission::getName)
                            .map(UUID::fromString)
                            .collect(Collectors.toSet()));
                    cluster.setAverageSimilarity(submissionCluster.getAverageSimilarity());

                    return cluster;
                })
                .toList();
    }

    private Map<UUID, PlagiarismUser> getSubmissionsForCheck(Exercise exercise) {
        Map<UUID, UUID> userIdSubmissionMap = new HashMap<>();
        Map<UUID, PlagiarismUser> submissionUserMap = new HashMap<>();

        submissionService.getSubmissionsFromExercise(exercise)
                .stream()
                .sorted(Comparator.comparing(Submission::getTimestamp))
                .reduce(new HashMap<>(), getAccumulator(), getCombiner()).values()
                .stream()
                .filter(getNotCompileErrorPredicate())
                .forEach(submission -> userIdSubmissionMap.put(submission.getUserId(), submission.getId()));

        userService.getUsersFromIdList(userIdSubmissionMap.keySet().stream().toList())
                .forEach(userDto -> submissionUserMap.put(userIdSubmissionMap.get(userDto.getId()), PlagiarismUser.fromUserDto(userDto)));

        return submissionUserMap;
    }

    private Predicate<? super Submission> getNotCompileErrorPredicate() {
        return submission -> submissionResultService.getResultsFromSubmission(submission)
                .stream().noneMatch(submissionResult -> SubmissionStatus.COMPILE_ERROR.equals(submissionResult.getStatus()));
    }

    private BiFunction<HashMap<UUID, Submission>, ? super Submission, HashMap<UUID, Submission>> getAccumulator() {
        return (map, submission) -> {
            map.merge(submission.getUserId(), submission, ((submission1, submission2) -> submission1));
            return map;
        };
    }

    private BinaryOperator<HashMap<UUID, Submission>> getCombiner() {
        return (m, m2) -> {
            m.putAll(m2);
            return m;
        };
    }

}
