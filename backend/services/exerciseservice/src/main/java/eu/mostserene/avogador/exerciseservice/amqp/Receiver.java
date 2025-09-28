package eu.mostserene.avogador.exerciseservice.amqp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.exerciseservice.projectservice.notebookprojects.NotebookProject;
import eu.mostserene.avogador.exerciseservice.projectservice.projects.Project;
import eu.mostserene.avogador.exerciseservice.projectservice.projects.ProjectService;
import eu.mostserene.avogador.exerciseservice.projectservice.projects.ProjectType;
import eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions.ProjectSubmission;
import eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions.ProjectSubmissionResultDto;
import eu.mostserene.avogador.exerciseservice.projectservice.projectsubmissions.ProjectSubmissionService;
import eu.mostserene.avogador.exerciseservice.strox.Strox;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionResult;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionResultDto;
import eu.mostserene.avogador.exerciseservice.submissionresults.SubmissionResultService;
import eu.mostserene.avogador.exerciseservice.submissions.Submission;
import eu.mostserene.avogador.exerciseservice.submissions.SubmissionService;
import eu.mostserene.avogador.exerciseservice.testcases.Testcase;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseDetailDto;
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseService;
import eu.mostserene.avogador.exerciseservice.trials.TrialService;
import eu.mostserene.avogador.exerciseservice.utils.LoggerColors;
import eu.mostserene.avogador.exerciseservice.utils.LoggerUtils;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import eu.mostserene.avogador.exerciseservice.utils.WebSocketMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@Slf4j
@Service
public class Receiver {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private TrialService trialService;

    @Autowired
    private SubmissionResultService submissionResultService;

    @Autowired
    private TestcaseService testcaseService;

    @Autowired
    private ProjectSubmissionService projectSubmissionService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private Sender sender;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "pingExercises", durable = "true"),
            exchange = @Exchange(value = "exercises", type = ExchangeTypes.TOPIC),
            key = "exercises.ping."))
    private void pingExercises() {
        log.info(LoggerColors.cyan("Hello from rabbit"));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "trialsDeletionHandler", durable = "true"),
            exchange = @Exchange(value = "exercises", type = ExchangeTypes.TOPIC),
            key = "trials.delete"))
    private void trialsDeletionHandler(String courseStringId) {
        UUID courseId = UUID.fromString(courseStringId);
        trialService.deleteTrialsByCourseId(courseId);
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "submissionSavedHandler", durable = "true"),
            exchange = @Exchange(value = "exercises", type = ExchangeTypes.TOPIC),
            key = "exercises.submission.save"))
    private void submissionSavedHandler(SubmissionSavedDto submissionSavedDto) {
        try {
            Submission submission = submissionService.getSubmission(submissionSavedDto.getSubmissionId())
                    .orElseThrow(RuntimeException::new);

            TimeUnit.SECONDS.sleep(1);

            sender.send("executor", "exec.submission.execute",
                    new SubmissionExecutionDto(
                            submission.getId(),
                            submission.getExercise().getTrial().getCourseId(),
                            submission.getExercise().getTrial().getId(),
                            submission.getExercise().getId(),
                            submission.getExercise().getLanguage().name(),
                            submissionSavedDto.getStrox().getSourceFileName(),
                            submission.getExercise().getTimeLimit(),
                            testcaseService.getTestcasesFromExercise(submission.getExercise())
                                    .stream()
                                    .map(TestcaseDetailDto::getId)
                                    .toList()
                    ), (BiConsumer<String, Throwable>) (s, throwable) -> {
                        log.info(LoggerColors.success(s));
                        log.info(LoggerColors.error(String.valueOf(throwable)));
                    });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "submissionResultHandler", durable = "true"),
            exchange = @Exchange(value = "exercises", type = ExchangeTypes.TOPIC),
            key = "exercises.submission.result"))
    private void submissionResultHandler(SubmissionResultDto submissionResultDto) {
        try {
            Submission submission = submissionService.getSubmission(submissionResultDto.getSubmissionId())
                    .orElseThrow(NotFoundException::new);

            Testcase testcase = testcaseService.getSimpleTestcase(submissionResultDto.getTestcaseId())
                    .orElseThrow(NotFoundException::new);

            SubmissionResult storedResult = submissionResultService.getResultsFromSubmission(submission)
                    .stream().filter(submissionResult -> submissionResult.getTestcase().getId().equals(testcase.getId()))
                    .findFirst().orElseGet(() -> new SubmissionResult(submission, testcase, submissionResultDto.getStatus()));

            storedResult.setStatus(submissionResultDto.getStatus());
            submissionResultDto.setId(storedResult.getId());
            submissionResultService.saveSubmissionResult(storedResult);

            submissionResultDto.setOutput(null);

            sender.send("users", "users.notify.socket", new WebSocketMessage("/" + submissionResultDto.getSubmissionId() + "/results",
                    mapper.writeValueAsString(submissionResultDto)
            ));
        } catch (IOException e) {
            log.error(LoggerColors.error(e.getMessage()));
            LoggerUtils.logErrorToSentry(e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ProjectSubmissionSavedHandler", durable = "true"),
            exchange = @Exchange(value = "exercises", type = ExchangeTypes.TOPIC),
            key = "projects.submission.save"))
    private void ProjectSubmissionSavedHandler(ProjectSubmissionSavedDto projectSubmissionSavedDto) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ProjectSubmission projectSubmission = projectSubmissionService.getProjectSubmissionById(projectSubmissionSavedDto.getSubmissionId())
                .orElseThrow(NotFoundException::new);

        sender.send("executor", "exec.project.execute", new ProjectSubmissionExecutionDto(
                projectSubmissionSavedDto.getSubmissionId(),
                projectSubmissionSavedDto.getCourseId(),
                projectSubmissionSavedDto.getProjectId(),
                getFullProjectTypeForExecutor(projectSubmission.getProject())
        ));

        try {
            sender.send("users", "users.notify.socket", new WebSocketMessage("/" + projectSubmission.getId() + "/status",
                    mapper.writeValueAsString(projectSubmission)
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFullProjectTypeForExecutor(Project project) {
        if (Objects.requireNonNull(project.getProjectType()) == ProjectType.NOTEBOOK) {
            return project.getProjectType() + "_" + ((NotebookProject) project).getKernel().name();
        } else {
            throw new RuntimeException("project type not found");
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "projectSubmissionResultHandler", durable = "true"),
            exchange = @Exchange(value = "exercises", type = ExchangeTypes.TOPIC),
            key = "projects.submission.result"))
    private void projectSubmissionResultHandler(ProjectSubmissionResultDto projectSubmissionResultDto) {
        try {
            ProjectSubmission projectSubmission = projectSubmissionService
                    .setProjectSubmissionStatus(projectSubmissionResultDto.getId(), projectSubmissionResultDto.getStatus());

            sender.send("users", "users.notify.socket", new WebSocketMessage("/" + projectSubmission.getId() + "/status",
                    mapper.writeValueAsString(projectSubmission)
            ));
        } catch (Exception e) {
            log.error(LoggerColors.error(e.getMessage()));
            LoggerUtils.logErrorToSentry(e);
        }
    }

    @Data
    private static class SubmissionSavedDto {
        private UUID submissionId;
        private Strox strox;

        public SubmissionSavedDto() {
        }

        public SubmissionSavedDto(UUID submissionId, Strox strox) {
            this.submissionId = submissionId;
            this.strox = strox;
        }
    }

    @Data
    private static class SubmissionExecutionDto {
        private UUID id;
        private UUID courseId;
        private UUID trialId;
        private UUID exerciseId;
        private String language;
        private String filename;
        private Integer timeLimit;
        private List<UUID> testcases;

        public SubmissionExecutionDto() {
        }

        public SubmissionExecutionDto(UUID id, UUID courseId, UUID trialId, UUID exerciseId, String language, String filename, Integer timeLimit, List<UUID> testcases) {
            this.id = id;
            this.courseId = courseId;
            this.trialId = trialId;
            this.exerciseId = exerciseId;
            this.language = language;
            this.filename = filename;
            this.timeLimit = timeLimit;
            this.testcases = testcases;
        }
    }


    @Data
    private static class ProjectSubmissionSavedDto {
        private UUID courseId;
        private UUID projectId;
        private UUID submissionId;

        public ProjectSubmissionSavedDto() {
        }
    }

    @Data
    private static class ProjectSubmissionExecutionDto {
        private UUID id;
        private UUID courseId;
        private UUID projectId;
        private String projectType;

        public ProjectSubmissionExecutionDto(UUID id, UUID courseId, UUID projectId, String projectType) {
            this.id = id;
            this.courseId = courseId;
            this.projectId = projectId;
            this.projectType = projectType;
        }
    }
}