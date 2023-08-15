package eu.mostserene.avogador.userservice.security.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.mostserene.avogador.userservice.users.AuthUserDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthUserDTOHeaderConverter implements Converter<String, AuthUserDTO> {

    @Override
    public AuthUserDTO convert(@NonNull String source) {
        try {
            return new ObjectMapper().readValue(source, AuthUserDTO.class);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while parsing user header");
        }
    }
}
