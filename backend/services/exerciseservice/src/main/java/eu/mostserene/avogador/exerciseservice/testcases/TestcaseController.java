package eu.mostserene.avogador.exerciseservice.testcases;

import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.trials.TrialService;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.utils.BadRequestException;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/exercises/{exerciseId}/testcases")
@Slf4j
public class TestcaseController {
    @Autowired
    private TestcaseService testcaseService;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private TrialService trialService;

    @Autowired
    private UserCourseService userCourseService;

    @GetMapping()
    private List<TestcaseDetailDto> getTestcasesFromExercise(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId) {
        var exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(() -> new NotFoundException("Not found exercise with id: " + exerciseId));
        var trial = trialService.getTrialById(exercise.getTrial().getId())
                .orElseThrow(() -> new NotFoundException("Not found trial with id: " + exercise.getTrial().getId()));
        var courseRole = userCourseService.getUserCourseRoleDetail(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user)).getRole();

        if (courseRole.getClearance() < CourseRole.STUDENT.getClearance() && !user.getIsSuperuser()) {
            throw new ForbiddenException(user);
        }

        try {
            return testcaseService.getTestcasesFromExercise(exercise)
                    .stream()
                    .filter(tc -> tc.getIsVisible() || courseRole.getClearance() >= CourseRole.COLLABORATOR.getClearance() || user.getIsSuperuser())
                    .sorted(Comparator.comparingInt(TestcaseDetailDto::getIndex))
                    .toList();
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Missing testcases");
        }
    }

    @GetMapping("/{testcaseId}")
    private TestcaseDetailDto getTestcaseById(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId, @PathVariable UUID testcaseId) {
        var exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(() -> new NotFoundException("Not found exercise with id: " + exerciseId));
        var trial = trialService.getTrialById(exercise.getTrial().getId())
                .orElseThrow(() -> new NotFoundException("Not found trial with id: " + exercise.getTrial().getId()));
        var courseRole = userCourseService.getUserCourseRoleDetail(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user)).getRole();

        if (courseRole.getClearance() < CourseRole.STUDENT.getClearance() && !user.getIsSuperuser()) {
            throw new ForbiddenException(user);
        }

        var testcase = testcaseService.getTestcase(exercise, testcaseId)
                .orElseThrow(() -> new NotFoundException("Not found testcase with id: " + testcaseId));
        if (!testcase.getIsVisible() && courseRole.getClearance() <= CourseRole.STUDENT.getClearance() && !user.getIsSuperuser()) {
            throw new ForbiddenException(user);
        }

        return testcase;
    }

    @PatchMapping("/order")
    private void updateTestcaseOrder(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId, @RequestBody List<UUID> testcaseIds) {
        var exercise = getExerciseIfCollaboratorClearance(exerciseId, user);

        Map<UUID, Testcase> testcases = testcaseService.getSimpleTestcasesFromExercise(exercise)
                .stream()
                .collect(Collectors.toMap(Testcase::getId, Function.identity()));

        Set<UUID> uniqueIds = new HashSet<>(testcaseIds);

        if (testcaseIds.size() != testcases.size() || uniqueIds.size() != testcases.size() || !uniqueIds.equals(testcases.keySet())) {
            throw new BadRequestException("List size mismatch");
        }

        for (int i = 0; i < testcaseIds.size(); i++) {
            testcaseService.updateTestcaseIndex(testcases.get(testcaseIds.get(i)), i);
        }
    }

    @PutMapping("")
    private TestcaseDetailDto insertTestcase(
            @RequestHeader(name = "User") UserDto user,
            @PathVariable UUID exerciseId,
            @RequestBody TestcaseDetailDto testcaseDto) {
        var exercise = getExerciseIfCollaboratorClearance(exerciseId, user);
        var courseDetail = userCourseService.getUserCourseRoleDetail(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseDetail.getIsArchived()) {
            throw new ResponseStatusException(HttpStatus.GONE, "This course has been archived");
        }

        if (testcaseDto.getIndex() == null) {
            testcaseDto.setIndex((int) Short.MAX_VALUE);
        }

        if (testcaseDto.getId() == null) {
            return testcaseService.createTestcase(testcaseDto, exercise);
        }
        var testcase = testcaseService.getSimpleTestcase(testcaseDto.getId())
                .orElseThrow(() -> new NotFoundException("Testcase with id " + testcaseDto.getId().toString() + " not found"));

        return testcaseService.updateTestcase(testcase, testcaseDto);
    }

    @DeleteMapping("/{testcaseId}")
    private void deleteTestcase(@RequestHeader(name = "User") UserDto user, @PathVariable UUID exerciseId, @PathVariable UUID testcaseId) {
        var exercise = getExerciseIfCollaboratorClearance(exerciseId, user);

        var courseDetail = userCourseService.getUserCourseRoleDetail(exercise.getTrial().getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (courseDetail.getIsArchived()) {
            throw new ResponseStatusException(HttpStatus.GONE, "This course has been archived");
        }

        testcaseService.deleteTestcase(exercise, testcaseId);
    }

    private Exercise getExerciseIfCollaboratorClearance(UUID exerciseId, UserDto user) {
        var exercise = exerciseService.getExercise(exerciseId)
                .orElseThrow(() -> new NotFoundException("Not found exercise with id: " + exerciseId));
        var trial = trialService.getTrialById(exercise.getTrial().getId())
                .orElseThrow(() -> new NotFoundException("Not found trial with id: " + exercise.getTrial().getId()));
        var courseRole = userCourseService.getUserCourseRoleDetail(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user)).getRole();

        if (courseRole.getClearance() < CourseRole.COLLABORATOR.getClearance() && !user.getIsSuperuser()) {
            throw new ForbiddenException(user);
        }
        return exercise;
    }

}
