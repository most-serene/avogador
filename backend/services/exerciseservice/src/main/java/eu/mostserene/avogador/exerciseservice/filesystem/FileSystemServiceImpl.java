package eu.mostserene.avogador.exerciseservice.filesystem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.exerciseservice.amqp.Sender;
import eu.mostserene.avogador.exerciseservice.exercises.Exercise;
import eu.mostserene.avogador.exerciseservice.strox.Strox;
import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseDetailDto;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseIODto;
import eu.mostserene.avogador.exerciseservice.trials.ProgrammingLanguage;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class FileSystemServiceImpl implements FileSystemService {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void createTrial(Trial trial) {
        try {
            (new Sender())
                    .send("filesystem", "fs.trial.create", mapper.writeValueAsString(new TrialStorageDTO(trial.getCourseId(), trial.getId())));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTrial(Trial trial) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public void createExercise(Exercise exercise) {
        try {
            (new Sender())
                    .send("filesystem", "fs.exercise.create",
                            mapper.writeValueAsString(new ExerciseStorageDTO(
                                    exercise.getTrial().getCourseId(), exercise.getTrial().getId(), exercise.getId())));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createExerciseTemplate(Exercise exercise, Strox template) {
        // FIXME: one day the professor will set the filename from the webapp
        String filename = switch (exercise.getTrial().getLanguage()) {
            case C -> "main.c";
            case CPP -> "main.cpp";
            case JAVA -> "Main.java";
            case PYTHON -> "main.py";
        };

        template.setSourceFileName(filename);
        try {
            (new Sender())
                    .send("filesystem", "fs.template.create",
                            mapper.writeValueAsString(new ExerciseTemplateStorageDTO(
                                    exercise.getTrial().getCourseId(), exercise.getTrial().getId(), exercise.getId(), template)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteExercise(Exercise exercise) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public void createTestcase(Exercise exercise, TestcaseDetailDto testcase) {
        try {
            (new Sender())
                    .send("filesystem", "fs.testcase.create",
                            mapper.writeValueAsString(new TestcaseStorageDto(
                                    exercise.getTrial().getCourseId(),
                                    exercise.getTrial().getId(),
                                    testcase.getExerciseId(),
                                    testcase.getId(),
                                    testcase.getInput(),
                                    testcase.getOutput()
                            )));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTestcase(TestcaseDetailDto testcase) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public Optional<TestcaseIODto> getTestcase(Exercise exercise, UUID testcaseId) {
        TestcaseIODto testcaseIO = new RestTemplateBuilder()
                .build()
                .getForObject("http://filesystem/courses/" + exercise.getTrial().getCourseId() +
                                "/trials/ " + exercise.getTrial().getId() +
                                "/exercises/" + exercise.getId() +
                                "/testcases/" + testcaseId,
                        TestcaseIODto.class);

        return testcaseIO != null ? Optional.of(testcaseIO) : Optional.empty();
    }

    @Override
    public void updateTestcase(Exercise exercise, TestcaseDetailDto testcase) {
        try {
            (new Sender())
                    .send("filesystem", "fs.testcase.create",
                            mapper.writeValueAsString(new TestcaseStorageDto(
                                    exercise.getTrial().getCourseId(),
                                    exercise.getTrial().getId(),
                                    testcase.getExerciseId(),
                                    testcase.getId(),
                                    testcase.getInput(),
                                    testcase.getOutput()
                            )));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createSubmission(Submission submission, Strox strox) {
        // FIXME: one day the professor will set the filename from the webapp
        String filename = switch (submission.getExercise().getTrial().getLanguage()) {
            case C -> "main.c";
            case CPP -> "main.cpp";
            case JAVA -> "Main.java";
            case PYTHON -> "main.py";
        };

        strox.setSourceFileName(filename);
        try {
            (new Sender())
                    .send("filesystem", "fs.submission.create",
                            mapper.writeValueAsString(new SubmissionStorageDto(
                                    submission.getExercise().getTrial().getCourseId(),
                                    submission.getExercise().getTrial().getId(),
                                    submission.getExercise().getId(),
                                    submission.getId(),
                                    strox
                            )));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Strox> getMergedSubmission(Submission submission) {
        Strox stroxTemplate = new RestTemplateBuilder()
                .build()
                .getForObject("http://filesystem/courses/" + submission.getExercise().getTrial().getCourseId() +
                                "/trials/ " + submission.getExercise().getTrial().getId() +
                                "/exercises/" + submission.getExercise().getId() +
                                "/template",
                        Strox.class);

        if (stroxTemplate == null) return Optional.empty();

        Strox stroxSubmission = new RestTemplateBuilder()
                .build()
                .getForObject("http://filesystem/courses/" + submission.getExercise().getTrial().getCourseId() +
                                "/trials/ " + submission.getExercise().getTrial().getId() +
                                "/exercises/" + submission.getExercise().getId() +
                                "/submissions/" + submission.getId() +
                                "/strox",
                        Strox.class);

        if (stroxSubmission == null) return Optional.empty();

        return Optional.of(Strox.merge(stroxTemplate, stroxSubmission));
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
}
