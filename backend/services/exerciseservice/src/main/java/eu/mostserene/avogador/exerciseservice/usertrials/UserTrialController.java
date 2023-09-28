package eu.mostserene.avogador.exerciseservice.usertrials;

import eu.mostserene.avogador.exerciseservice.courses.CourseRole;
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService;
import eu.mostserene.avogador.exerciseservice.security.ForbiddenException;
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


    /**
     * gets all the users subscribed to a trial
     * @param user the requesting user
     * @param trialId the id of the trial to which the users belong
     * @return the list of users belonging to a trial
     * */
    @GetMapping("/{trialId}/users")
    private List<UserTrialDetailDto> getUsersFromTrial(@RequestHeader(name = "User") UserDto user, @PathVariable UUID trialId){
        var trial = trialService.getTrialById(trialId)
                .orElseThrow(NotFoundException::new);
        var userRole = userCourseService.getUserCourseRole(trial.getCourseId(), user.getId())
                .orElseThrow(() -> new ForbiddenException(user));

        if (userRole.getClearance() <= CourseRole.STUDENT.getClearance())
            throw new ForbiddenException(user);

        var userTrials = userTrialService.getUsersFromTrial(trial);
        var users = userService.getUsersFromIdList(userTrials.stream().map(UserTrial::getUserId).toList()).
                stream().collect(Collectors.toMap(UserDto::getId, u -> u));

        return userTrials.stream()
                .map(userTrial -> userTrial.getUserTrialDetail(users.get(userTrial.getUserId())))
                .toList();
    }

    /**
     * gets the trials where the users belong
     * @param user the requesting user
     * @param userId the id of the user belonging to the trials
     * @return the list of trials where the user is subscribed
     * */
    @GetMapping("/users/{userId}")
    private List<UserTrial> getTrialsFromUser(@RequestHeader(name = "User") UserDto user, @PathVariable UUID userId){
        if (!user.getId().equals(userId) && !user.getIsSuperuser()){
            throw new ForbiddenException(user);
        }

        return userTrialService.getTrialsFromUser(user);
    }

}
