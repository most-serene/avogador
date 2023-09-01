package eu.mostserene.avogador.userservice.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Slf4j
public class InternalUserController {
    private @Autowired UserService userService;

    @PatchMapping("")
    private List<AuthUserDTO> getUsersFromIdList(@RequestBody List<UUID> ids, @RequestParam Optional<Integer> limit, @RequestParam Optional<Integer> offset,
                                                 @RequestParam Optional<String> orderBy, @RequestParam Optional<String> direction) {
        Sort sort;
        List<User> users;

        int offsetVal = offset.orElse(0);
        int limitVal = limit.orElse(0);

        if (offsetVal < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Negative offset");
        }
        if (limitVal < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Negative limit");
        }

        if (orderBy.isPresent()){
            String directionVal = direction.orElse("ASC");
            if (!directionVal.equals("ASC") && !directionVal.equals("DESC")){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong order direction");
            }

            sort = Sort.by(directionVal.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy.get());
            users = userService.getUsersByIds(ids, PageRequest.of(offsetVal, limitVal == 0 ? ids.size() : limitVal, sort));
        }
        else {
            users = userService.getUsersByIds(ids, PageRequest.of(offsetVal, limitVal == 0 ? ids.size() : limitVal));
        }

        return users.stream().map(User::generateAuthUserDTO).toList();
    }
}
