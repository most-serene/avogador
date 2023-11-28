package eu.mostserene.avogador.exerciseservice.antiplagiarism;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.Language;
import de.jplag.clustering.ClusteringOptions;
import de.jplag.exceptions.BasecodeException;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;
import de.jplag.reporting.reportobject.ReportObjectFactory;
import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.storage.StorageService;
import eu.mostserene.avogador.exerciseservice.strox.Strox;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionResultService;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionStatus;
import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import eu.mostserene.avogador.exerciseservice.submissions.SubmissionService;
import eu.mostserene.avogador.exerciseservice.utils.LoggerColors;
import io.sentry.Sentry;
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
import java.nio.file.Files;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

@Service
@Slf4j
public class AntiPlagiarismServiceImpl implements AntiPlagiarismService {
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private StorageService storageService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private SubmissionResultService submissionResultService;

    @Override
    public void executeSimilarityTool(Exercise exercise) {
        new Thread(() -> similarityCheckJobWrapper(exercise)).start();
    }

    private void similarityCheckJobWrapper(Exercise exercise) {
        log.debug(LoggerColors.cyan("Similarity Job started on exercise - " + exercise.getId()));
        try {
            similarityCheckJob(exercise);
            log.debug(LoggerColors.success("Similarity Job done on exercise - " + exercise.getId()));
        } catch (Exception e) {
            log.error(LoggerColors.error("Similarity Job failed on exercise - " + exercise.getId()));
            log.error(LoggerColors.error(e.toString()));
            Sentry.captureException(e);
        }
    }

    @Override
    public Optional<Resource> getSimilarityReport(Exercise exercise) {
        return storageService.getSimilarityReport(exercise);
    }

    private void similarityCheckJob(Exercise exercise) throws IOException {
        File workingDirectory = Files.createTempDirectory("avogador").toFile();
        try {
            File baseCode = getAndUnpackTemplate(exercise, workingDirectory);
            File submissionsDirectories = getAndUnpackSubmissions(exercise, workingDirectory);

            Language language = getLanguage(exercise);
            JPlagOptions options = new JPlagOptions(language,
                    Set.of(submissionsDirectories),
                    Set.of())
                    .withBaseCodeSubmissionDirectory(baseCode)
                    .withClusteringOptions(new ClusteringOptions().withEnabled(true));

            handleToolRun(options, exercise, workingDirectory);
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

    private void handleToolRun(JPlagOptions options, Exercise exercise, File workingDirectory) throws ExitException {
        boolean retry = true;
        while (retry) {
            try {
                runTool(options, workingDirectory);
                retry = false;
                storageService.uploadSimilarityReport(exercise, new File(workingDirectory + "/results.zip"));
            } catch (BasecodeException basecodeException) {
                options = new JPlagOptions(options.language(), options.submissionDirectories(), Set.of())
                        .withClusteringOptions(new ClusteringOptions().withEnabled(true));
            }
        }
    }

    private File getAndUnpackSubmissions(Exercise exercise, File workingDirectory) throws IOException {
        Resource submissions = storageService.getExerciseLatestSubmissionsSources(exercise, getSubmissionsForCheck(exercise));
        File submissionsArchive = new File(workingDirectory + "/submissions.tar.gz");
        FileUtils.copyInputStreamToFile(submissions.getInputStream(), submissionsArchive);
        File submissionsDirectory = new File(workingDirectory + "/submissions");
        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
        archiver.extract(submissionsArchive, submissionsDirectory);
        return submissionsDirectory;
    }

    private void runTool(JPlagOptions options, File workingDirectory) throws ExitException {
        JPlagResult result = (new JPlag(options)).run();
        ReportObjectFactory reportObjectFactory = new ReportObjectFactory();
        reportObjectFactory.createAndSaveReport(result, workingDirectory + "/results");
    }

    private List<UUID> getSubmissionsForCheck(Exercise exercise) {
        return submissionService.getSubmissionsFromExercise(exercise)
                .stream()
                .sorted(Comparator.comparing(Submission::getTimestamp))
                .reduce(new HashMap<>(), getAccumulator(), getCombiner()).values()
                .stream()
                .filter(getNotCompileErrorPredicate())
                .map(Submission::getId)
                .toList();
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
