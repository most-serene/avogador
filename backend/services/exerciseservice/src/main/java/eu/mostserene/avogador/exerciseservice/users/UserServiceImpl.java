package eu.mostserene.avogador.exerciseservice.users;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Data
    private static class UserDtoList {
        private List<UserDto> users;

        public UserDtoList() {
            this.users = new ArrayList<>();
        }

        public UserDtoList(List<UserDto> users) {
            this.users = users;
        }
    }

    /**
     * Get the ID of a request
     *
     * @param request the current request
     * @return the ID as String
     */
    @Override
    public String getRequestID(HttpServletRequest request) {
        return request.getHeader("Request-ID");
    }

    @Override
    public List<UserDto> getUsersFromIdList(List<UUID> ids) {
        HttpClient client = HttpClients.createDefault();
        RestTemplate template= new RestTemplate();
        template.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

        if (ids.isEmpty()){
            return List.of();
        }

        return Arrays.stream(Objects.requireNonNull(template.patchForObject("http://users/users?limit=0", ids, UserDto[].class)))
                .toList();
    }

    @Override
    public List<UserDto> getUsersFromIdList(List<UUID> ids, Optional<Integer> limit, Optional<Integer> offset, Optional<String> orderBy, Optional<String> direction) {
        HttpClient client = HttpClients.createDefault();
        RestTemplate template= new RestTemplate();
        template.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

        String url = "http://users/users?" +
                limit.map(l -> "&limit=" + l).orElse("") +
                offset.map(o -> "&offset=" + o).orElse("") +
                orderBy.map(ord -> "&orderBy=" + ord).orElse("") +
                direction.map(dir -> "&direction=" + dir).orElse("");

        return Arrays.stream(Objects.requireNonNull(template.patchForObject(url, ids, UserDto[].class)))
                .toList();
    }

}
