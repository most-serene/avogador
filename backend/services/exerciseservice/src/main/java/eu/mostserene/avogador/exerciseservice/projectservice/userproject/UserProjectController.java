package eu.mostserene.avogador.exerciseservice.projectservice.userproject;

import eu.mostserene.avogador.exerciseservice.projectservice.projects.ProjectService;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.users.UserService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/public/projects/{projectId}/users")
public class UserProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @PutMapping("/marks")
    private void uploadMarks(@RequestHeader(name = "User") UserDto user,
                             @PathVariable UUID projectId, @RequestBody List<Pair<UUID, Integer>> marks) {
        throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "Not yet supported");
    }

}
