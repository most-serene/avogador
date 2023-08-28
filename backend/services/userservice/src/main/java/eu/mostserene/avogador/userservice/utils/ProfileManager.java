package eu.mostserene.avogador.userservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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

    /**
     * Executes one of the callbacks given the current profile
     *
     * @param onDevelop    supplier to be executed under develop profile
     * @param onTesting    supplier to be executed under develop profile
     * @param onStaging    supplier to be executed under develop profile
     * @param onProduction supplier to be executed under develop profile
     * @param <T>          the output type of by the callbacks
     * @return the result of the callbacks
     */
    public <T> T executeOnProfile(Supplier<T> onDevelop,
                                  Supplier<T> onTesting,
                                  Supplier<T> onStaging,
                                  Supplier<T> onProduction) {
        return switch (activeProfiles) {
            case "develop" -> onDevelop.get();
            case "testing" -> onTesting.get();
            case "staging" -> onStaging.get();
            case "production" -> onProduction.get();
            default -> throw new RuntimeException("This should never happen");
        };
    }


    /**
     * Executes one of the callbacks given the current profile
     *
     * @param onDevelop    consumer to be executed under develop profile
     * @param onTesting    consumer to be executed under develop profile
     * @param onStaging    consumer to be executed under develop profile
     * @param onProduction consumer to be executed under develop profile
     * @param <T>          the input type of by the callbacks
     */
    public <T> void executeOnProfile(Consumer<T> onDevelop,
                                     Consumer<T> onTesting,
                                     Consumer<T> onStaging,
                                     Consumer<T> onProduction,
                                     T t) {
        switch (activeProfiles) {
            case "develop" -> onDevelop.accept(t);
            case "testing" -> onTesting.accept(t);
            case "staging" -> onStaging.accept(t);
            case "production" -> onProduction.accept(t);
            default -> throw new RuntimeException("This should never happen");
        }
    }
}
