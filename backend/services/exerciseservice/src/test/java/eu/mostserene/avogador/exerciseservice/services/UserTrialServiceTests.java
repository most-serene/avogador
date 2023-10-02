package eu.mostserene.avogador.exerciseservice.services;

import eu.mostserene.avogador.exerciseservice.practices.Practice;
import eu.mostserene.avogador.exerciseservice.trials.ProgrammingLanguage;
import eu.mostserene.avogador.exerciseservice.users.UserDto;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrial;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialRepository;
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class UserTrialServiceTests {
    @InjectMocks
    UserTrialServiceImpl userTrialService;
    @Mock
    UserTrialRepository repository;
    private @MockBean BuildProperties buildProperties;

    private final Practice practice = new Practice(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Practice One",
            true, true, ProgrammingLanguage.JAVA, Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private final UserTrial justCreatedUserTrial = new UserTrial(UUID.fromString("00000000-0000-0000-0000-000000000001"), practice, false);
    private final UserTrial alreadyPresentUserTrial = new UserTrial(UUID.fromString("00000000-0000-0000-0000-000000000001"), practice, false);
    private final UserDto studentUser = new UserDto(UUID.fromString("00000000-0000-0000-0000-000000000001"), "student@stud.unive.it", "Andy", "Bernard", false, false);

    @BeforeAll
    private void initFields() {
        alreadyPresentUserTrial.setStartTime(Date.from(Instant.now().minus(6, ChronoUnit.DAYS)));
        alreadyPresentUserTrial.setDeadline(Date.from(Instant.now().plus(10, ChronoUnit.MINUTES)));
    }

    @Nested
    class JoinTrial {
        @Test
        public void emptyRelation() throws Exception{
            when(repository.findByTrialAndUserId(any(), any()))
                    .thenReturn(Optional.empty());
            when(repository.save(any()))
                    .thenReturn(justCreatedUserTrial);

            var result = userTrialService.joinTrial(studentUser, practice);
            assertEquals(practice.getDeadline(), result.getDeadline());
            assertNotNull(result.getStartTime());
        }

        @Test
        public void nonEmptyRelation() throws Exception{
            when(repository.findByTrialAndUserId(any(), any()))
                    .thenReturn(Optional.of(alreadyPresentUserTrial));

            var result = userTrialService.joinTrial(studentUser, practice);
            assertNotEquals(practice.getDeadline(), result.getDeadline());
            assertEquals(alreadyPresentUserTrial.getDeadline(), result.getDeadline());
        }
    }
}
