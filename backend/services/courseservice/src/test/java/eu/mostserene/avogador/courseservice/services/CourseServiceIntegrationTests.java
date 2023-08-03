package eu.mostserene.avogador.courseservice.services;

import eu.mostserene.avogador.courseservice.courses.CourseService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
public class CourseServiceIntegrationTests {

    private @Autowired CourseService courseService;

    @ParameterizedTest
    @MethodSource("joinCodePairs")
    void getJoinCode(Long courseId, String hash) {
        var result = courseService.getJoinCode(courseId);

        assertTrue(result.isPresent());
        assertEquals(hash, result.get());

        System.out.println(result.get());
    }

    private static Stream<Arguments> joinCodePairs() {
        return Stream.of(
                arguments(1L, "7ba32aca07cc92001d74537d5ff775343390210f6812450d844bb9a24598c3ff"),
                arguments(2L, "deb76a5414e161b6245f9458ccee77385b97e2a8a90f82506f724b66c8496601"),
                arguments(42L, "df20639c0f5618657a156d945f2ca35cb91dd24320e5f152e1b3df1537d5e3f0")
        );
    }
}
