package eu.mostserene.avogador.courseservice.services;

import eu.mostserene.avogador.courseservice.courses.CourseService;
import eu.mostserene.avogador.courseservice.utils.ProfileManager;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
public class CourseServiceIntegrationTests {

    private @Autowired CourseService courseService;

    @ParameterizedTest
    @MethodSource("joinCodePairs")
    void getJoinCode(UUID courseId, String hash) {
        var result = courseService.getJoinCode(courseId);

        assertTrue(result.isPresent());
        assertEquals(hash, result.get());

        System.out.println(result.get());
    }

    private static Stream<Arguments> joinCodePairs() {
        return Stream.of(
                arguments(UUID.fromString("00000000-0000-0000-0000-000000000001"), "de36f328af472c609a2afba8d503cb097fa1c0fcdf6ff039b93e85c25f79932e"),
                arguments(UUID.fromString("00000000-0000-0000-0000-000000000002"), "80f5c8882dccd8bfceef27bf3826288218df8798ecc3b5271ad000698da444b5"),
                arguments(UUID.fromString("00000000-0000-0000-0000-000000000042"), "967f104528bf9157d5be4aa849c116174e6864b33cdd4d721281e957638f84bb")
        );
    }
}
