package eu.mostserene.avogador.exerciseservice.trials;


import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/public/trials")
@Slf4j
public class TrialController {
    @Autowired
    private TrialService trialService;
    @Autowired
    private UserCourseService userCourseService;

    @GetMapping("/courses/{courseId}")
    private List<Trial> getTrialsFromCourse(@RequestHeader(name = "User") UserDto user, @PathVariable UUID courseId){
        var userRole = userCourseService.getUserCourseRole(courseId, user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (userRole.getClearance() < CourseRole.STUDENT.getClearance()){
            throw new ForbiddenException(user);
        }

        return trialService.getTrialsByCourseId(courseId);
    }

    @DeleteMapping("/{trialId}")
    private void deleteTrial(@RequestHeader(name = "User") UserDto user, @PathVariable UUID trialId){
        var trial = trialService.getTrialById(trialId)
                .orElseThrow(() -> new NotFoundException(trialId.toString()));

        var userRole = userCourseService.getUserCourseRole(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if(userRole.getClearance() < CourseRole.COLLABORATOR.getClearance()){
            throw new ForbiddenException(user);
        }

        trialService.deleteTrial(trial);
    }
}
