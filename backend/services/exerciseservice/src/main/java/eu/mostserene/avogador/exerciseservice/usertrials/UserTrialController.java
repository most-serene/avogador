package eu.mostserene.avogador.exerciseservice.usertrials;

import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
import eu.mostserene.avogador.exerciseservice.trials.Trial;
import eu.mostserene.avogador.exerciseservice.trials.TrialService;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.users.UserService;
import eu.mostserene.avogador.exerciseservice.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/trials")
@Slf4j
public class UserTrialController {
    @Autowired
    private UserTrialService userTrialService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private TrialService trialService;


    @GetMapping("/{trialId}/users")
    private List<UserTrialDetailDto> getUsersFromTrial(@RequestHeader(name = "User") UserDto user, @PathVariable UUID trialId){
        var trial = trialService.getTrialById(trialId)
                .orElseThrow(NotFoundException::new);
        var userRole = userCourseService.getUserCourseRole(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (userRole.getClearance() <= CourseRole.STUDENT.getClearance())
            throw new ForbiddenException(user);

        var userTrials = userTrialService.getUsersFromTrialId(trialId);
        var users = userService.getUsersFromIdList(userTrials.stream().map(UserTrial::getUserId).toList()).
                stream().collect(Collectors.toMap(UserDto::getId, u -> u));

        return userTrials.stream()
                .map(userTrial -> userTrial.getUserTrialDetail(users.get(userTrial.getUserId())))
                .toList();
    }

    @PutMapping("/users/{userId}")
    private List<UserTrial> getTrialsFromUser(@RequestHeader(name = "User") UserDto user, @PathVariable UUID userId){
        if (user.getId() != userId && !user.getIsSuperuser()){
            throw new ForbiddenException(user);
        }

        return userTrialService.getTrialsFromUserId(userId);
    }

}
