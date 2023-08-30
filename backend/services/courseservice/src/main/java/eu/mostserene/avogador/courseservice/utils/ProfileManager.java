package eu.mostserene.avogador.courseservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@Slf4j
public class ProfileManager {

    @Value("${spring.profiles.active}")
    private String activeProfiles;

    /**
     * Private Bean to check if the provided profile is acceptable
     *
     * @throws InvalidProfileException if the profile is invalid or absent
     */
    @Bean
    private void checkProfile() throws InvalidProfileException {
        log.info(activeProfiles);
        if (!EnumUtils.isValidEnum(AvogadorProfile.class, activeProfiles.toUpperCase())) {
            throw new InvalidProfileException();
        }
    }

    /**
     * Get the current active profiles
     * @return the current active profiles
     */
    public String getActiveProfiles() {
        return activeProfiles;
    }

    /**
     * Executes one of the callbacks given the current profile
     *
     * @param onDevelop    callback to be executed under develop profile
     * @param onTesting    callback to be executed under develop profile
     * @param onStaging    callback to be executed under develop profile
     * @param onProduction callback to be executed under develop profile
     * @param <T>          the input type of by the callbacks
     * @param <R>          the output type of the callbacks
     * @return the result of the callbacks
     */
    public <T, R> R executeOnProfile(Function<T, R> onDevelop,
                                     Function<T, R> onTesting,
                                     Function<T, R> onStaging,
                                     Function<T, R> onProduction, T t) {
        return switch (activeProfiles) {
            case "develop" -> onDevelop.apply(t);
            case "testing" -> onTesting.apply(t);
            case "staging" -> onStaging.apply(t);
            case "production" -> onProduction.apply(t);
            default -> throw new RuntimeException("This should never happen");
        };
    }
}
