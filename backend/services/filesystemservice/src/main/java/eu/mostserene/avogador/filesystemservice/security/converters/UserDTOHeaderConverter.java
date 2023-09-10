package eu.mostserene.avogador.filesystemservice.security.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.filesystemservice.users.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserDTOHeaderConverter implements Converter<String, UserDto> {

    @Override
    public UserDto convert(@NonNull String source) {
        try {
            return new ObjectMapper().readValue(source, UserDto.class);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while parsing user header");
        }
    }
}
