package eu.mostserene.avogador.userservice.profilemanager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Getter
@Slf4j
public class ProfileManager {
    @Value("${spring.profiles.active}")
    private String activeProfileName;

    public Profile getActiveProfile(){
        return switch (activeProfileName){
            case "develop" -> new DevelopProfile();
            case "testing" -> new TestingProfile();
            case "staging" -> new StagingProfile();
            case "production" -> new ProductionProfile();
            default -> throw new RuntimeException("This should never happen");
        };
    }
}
