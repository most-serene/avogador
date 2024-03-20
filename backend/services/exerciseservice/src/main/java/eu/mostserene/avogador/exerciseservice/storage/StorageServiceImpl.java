package eu.mostserene.avogador.exerciseservice.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises.CodingExercise;
import eu.mostserene.avogador.exerciseservice.amqp.Sender;
import eu.mostserene.avogador.exerciseservice.antiplagiarism.PlagiarismReport;
import eu.mostserene.avogador.exerciseservice.strox.Strox;
import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseDetailDto;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseIODto;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class StorageServiceImpl implements StorageService {
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private Sender sender;

    @Override
    public void createTrial(Trial trial) {
        sender.send("storage", "storage.trial.create", new TrialStorageDTO(trial.getCourseId(), trial.getId()));
    }

    @Override
    public void deleteTrial(Trial trial) {
        sender.send("storage", "storage.trial.delete", new TrialStorageDTO(trial.getCourseId(), trial.getId()));
    }

    @Override
    public void createExercise(CodingExercise exercise) {
        sender.send("storage", "storage.exercise.create",
                new ExerciseStorageDTO(
                        exercise.getTrial().getCourseId(), exercise.getTrial().getId(), exercise.getId()));

    }

    @Override
    public void createExerciseTemplate(CodingExercise exercise, Strox template) {
        // FIXME: one day the professor will set the filename from the webapp
        String filename = switch (exercise.getLanguage()) {
            case C -> "main.c";
            case CPP -> "main.cpp";
            case JAVA -> "Main.java";
            case PYTHON -> "main.py";
        };

        template.setSourceFileName(filename);
        sender.send("storage", "storage.template.create",
                new ExerciseTemplateStorageDTO(
                        exercise.getTrial().getCourseId(), exercise.getTrial().getId(), exercise.getId(), template));
    }

    @Override
    public void deleteExercise(CodingExercise exercise) {
        sender.send("storage", "storage.exercise.delete",
                new ExerciseStorageDTO(
                        exercise.getTrial().getCourseId(), exercise.getTrial().getId(), exercise.getId()));

    }

    @Override
    public void createTestcase(CodingExercise exercise, TestcaseDetailDto testcase) {
        sender.send("storage", "storage.testcase.create",
                new TestcaseStorageDto(
                        exercise.getTrial().getCourseId(),
                        exercise.getTrial().getId(),
                        testcase.getExerciseId(),
                        testcase.getId(),
                        testcase.getInput(),
                        testcase.getOutput()
                ));
    }

    @Override
    public void deleteTestcase(CodingExercise exercise, UUID testcaseId) {
        sender.send("storage", "storage.testcase.delete",
                new TestcaseStorageDto(
                        exercise.getTrial().getCourseId(),
                        exercise.getTrial().getId(),
                        exercise.getId(),
                        testcaseId,
                        "",
                        ""
                ));
    }

    @Override
    public Optional<TestcaseIODto> getTestcase(CodingExercise exercise, UUID testcaseId) {
        TestcaseIODto testcaseIO = new RestTemplateBuilder()
                .build()
                .getForObject("http://storage/courses/" + exercise.getTrial().getCourseId() +
                                "/trials/ " + exercise.getTrial().getId() +
                                "/exercises/" + exercise.getId() +
                                "/testcases/" + testcaseId,
                        TestcaseIODto.class);

        return testcaseIO != null ? Optional.of(testcaseIO) : Optional.empty();
    }

    @Override
    public void updateTestcase(CodingExercise exercise, TestcaseDetailDto testcase) {
        sender.send("storage", "storage.testcase.create",
                new TestcaseStorageDto(
                        exercise.getTrial().getCourseId(),
                        exercise.getTrial().getId(),
                        testcase.getExerciseId(),
                        testcase.getId(),
                        testcase.getInput(),
                        testcase.getOutput()
                ));
    }

    @Override
    public void createSubmission(Submission submission, Strox strox) {
        // FIXME: one day the professor will set the filename from the webapp
        String filename = switch (submission.getExercise().getLanguage()) {
            case C -> "main.c";
            case CPP -> "main.cpp";
            case JAVA -> "Main.java";
            case PYTHON -> "main.py";
        };

        strox.setSourceFileName(filename);
        sender.send("storage", "storage.submission.create",
                new SubmissionStorageDto(
                        submission.getExercise().getTrial().getCourseId(),
                        submission.getExercise().getTrial().getId(),
                        submission.getExercise().getId(),
                        submission.getId(),
                        strox
                ));
    }

    @Override
    public Optional<Strox> getSubmissionStrox(Submission submission) {
        Strox submissionStrox = new RestTemplateBuilder()
                .build()
                .getForObject("http://storage/courses/" + submission.getExercise().getTrial().getCourseId() +
                                "/trials/ " + submission.getExercise().getTrial().getId() +
                                "/exercises/" + submission.getExercise().getId() +
                                "/submissions/" + submission.getId() + "/strox",
                        Strox.class);

        if (submissionStrox == null) {
            return Optional.empty();
        }
        return Optional.of(submissionStrox);
    }

    @Override
    public Optional<Resource> getSubmissionSource(Submission submission) {
        Resource submissionSourceCode = new RestTemplateBuilder()
                .build()
                .getForObject("http://storage/courses/" + submission.getExercise().getTrial().getCourseId() +
                                "/trials/ " + submission.getExercise().getTrial().getId() +
                                "/exercises/" + submission.getExercise().getId() +
                                "/submissions/" + submission.getId() + "/source",
                        Resource.class);

        if (submissionSourceCode == null) {
            return Optional.empty();
        }
        return Optional.of(submissionSourceCode);
    }

    public Optional<Strox> getExerciseTemplate(CodingExercise exercise) {
        Strox stroxTemplate = new RestTemplateBuilder()
                .build()
                .getForObject("http://storage/courses/" + exercise.getTrial().getCourseId() +
                                "/trials/ " + exercise.getTrial().getId() +
                                "/exercises/" + exercise.getId() +
                                "/template",
                        Strox.class);

        if (stroxTemplate == null) {
            return Optional.empty();
        }
        return Optional.of(stroxTemplate);
    }

    @Override
    public Optional<Strox> getMergedSubmission(Submission submission) {
        Optional<Strox> stroxTemplate = getExerciseTemplate(submission.getExercise());

        if (stroxTemplate.isEmpty()) return Optional.empty();

        Strox stroxSubmission = new RestTemplateBuilder()
                .build()
                .getForObject("http://storage/courses/" + submission.getExercise().getTrial().getCourseId() +
                                "/trials/ " + submission.getExercise().getTrial().getId() +
                                "/exercises/" + submission.getExercise().getId() +
                                "/submissions/" + submission.getId() +
                                "/strox",
                        Strox.class);

        if (stroxSubmission == null) return Optional.empty();

        return Optional.of(Strox.merge(stroxTemplate.get(), stroxSubmission));
    }

    @Override
    public Resource getExerciseLatestSubmissionsSources(CodingExercise exercise, List<UUID> submissionIds) {
        return new RestTemplateBuilder()
                .build()
                .patchForObject("http://storage/courses/" + exercise.getTrial().getCourseId() +
                                "/trials/ " + exercise.getTrial().getId() +
                                "/exercises/" + exercise.getId() +
                                "/submissions/source", submissionIds,
                        Resource.class);
    }

    @Override
    public void uploadSimilarityReport(CodingExercise exercise, File reportZip) {
        try {
            sender.send("storage", "storage.exercise.similarity",
                    new SimilarityReportStorageDto(
                            exercise.getTrial().getCourseId(),
                            exercise.getTrial().getId(),
                            exercise.getId(),
                            reportZip
                    ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<PlagiarismReport> getSimilarityReport(CodingExercise exercise) {
        try {
            PlagiarismReport similarityReport = new RestTemplateBuilder()
                    .build()
                    .getForObject("http://storage/courses/" + exercise.getTrial().getCourseId() +
                                    "/trials/ " + exercise.getTrial().getId() +
                                    "/exercises/" + exercise.getId() +
                                    "/similarity-report",
                            PlagiarismReport.class);
            return (similarityReport == null) ? Optional.empty() : Optional.of(similarityReport);
        } catch (HttpClientErrorException.NotFound notFoundException) {
            return Optional.empty();
        }
    }

    @Data
    private static class TrialStorageDTO {
        private UUID courseId;
        private UUID trialId;

        public TrialStorageDTO() {
        }

        public TrialStorageDTO(UUID courseId, UUID trialId) {
            this.courseId = courseId;
            this.trialId = trialId;
        }
    }

    @Data
    private static class ExerciseStorageDTO {
        private UUID courseId;
        private UUID trialId;

        private UUID exerciseId;

        public ExerciseStorageDTO() {
        }

        public ExerciseStorageDTO(UUID courseId, UUID trialId, UUID exerciseId) {
            this.courseId = courseId;
            this.trialId = trialId;
            this.exerciseId = exerciseId;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    private static class ExerciseTemplateStorageDTO extends ExerciseStorageDTO {
        private Strox template;

        public ExerciseTemplateStorageDTO() {
        }

        public ExerciseTemplateStorageDTO(UUID courseId, UUID trialId, UUID exerciseId, Strox template) {
            super(courseId, trialId, exerciseId);
            this.template = template;
        }
    }

    @Data
    private static class TestcaseStorageDto {
        private UUID courseId;
        private UUID trialId;
        private UUID exerciseId;
        private UUID testcaseId;
        private String input;
        private String output;

        public TestcaseStorageDto() {
        }

        public TestcaseStorageDto(UUID courseId, UUID trialId, UUID exerciseId, UUID testcaseId, String input, String output) {
            this.courseId = courseId;
            this.trialId = trialId;
            this.exerciseId = exerciseId;
            this.testcaseId = testcaseId;
            this.input = input;
            this.output = output;
        }
    }

    @Data
    private static class SubmissionStorageDto {
        private UUID courseId;
        private UUID trialId;
        private UUID exerciseId;
        private UUID submissionId;
        private Strox submission;

        public SubmissionStorageDto() {
        }

        public SubmissionStorageDto(UUID courseId, UUID trialId, UUID exerciseId, UUID submissionId, Strox submission) {
            this.courseId = courseId;
            this.trialId = trialId;
            this.exerciseId = exerciseId;
            this.submissionId = submissionId;
            this.submission = submission;
        }
    }

    @Data
    private static class SimilarityReportStorageDto {
        private UUID courseId;
        private UUID trialId;
        private UUID exerciseId;
        private byte[] similarityReport;

        public SimilarityReportStorageDto() {
        }

        public SimilarityReportStorageDto(UUID courseId, UUID trialId, UUID exerciseId, File similarityReportFile) throws IOException {
            this.courseId = courseId;
            this.trialId = trialId;
            this.exerciseId = exerciseId;
            this.similarityReport = FileUtils.readFileToByteArray(similarityReportFile);
        }
    }
}
