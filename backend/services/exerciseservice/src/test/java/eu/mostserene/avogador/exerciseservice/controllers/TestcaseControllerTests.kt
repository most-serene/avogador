package eu.mostserene.avogador.exerciseservice.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import eu.mostserene.avogador.exerciseservice.courses.CourseRole
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService
import eu.mostserene.avogador.exerciseservice.exercises.Exercise
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService
import eu.mostserene.avogador.exerciseservice.practices.Practice
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseController
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseDetailDto
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseService
import eu.mostserene.avogador.exerciseservice.trials.ProgrammingLanguage
import eu.mostserene.avogador.exerciseservice.trials.Trial
import eu.mostserene.avogador.exerciseservice.trials.TrialService
import eu.mostserene.avogador.exerciseservice.users.UserDto
import eu.mostserene.avogador.exerciseservice.utils.ProfileManager
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Stream

@WebMvcTest(TestcaseController::class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestcaseControllerTests {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var buildProperties: BuildProperties

    @MockBean
    private lateinit var profileManager: ProfileManager

    @MockBean
    private lateinit var userCourseService: UserCourseService

    @MockBean
    private lateinit var exerciseService: ExerciseService

    @MockBean
    private lateinit var testcaseService: TestcaseService

    @MockBean
    private lateinit var trialService: TrialService

    private val mapper: ObjectWriter = ObjectMapper().writer().withDefaultPrettyPrinter()


    private val external = UserDto(
        UUID.fromString("00000000-0000-0000-0000-000000000000"),
        "creed.bratton@avogador.com",
        "Creed",
        "Bratton",
        false,
        false
    )
    private val student = UserDto(
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        "andy.bernard@avogador.com",
        "Andy",
        "Bernard",
        false,
        false
    )
    private val collaborator = UserDto(
        UUID.fromString("00000000-0000-0000-0000-000000000002"),
        "dwigth.schrute@avogador.com",
        "Dwight",
        "Schrute",
        false,
        false
    )
    private val professor = UserDto(
        UUID.fromString("00000000-0000-0000-0000-000000000003"),
        "jim.halpert@avogador.com",
        "Jim",
        "Halpert",
        true,
        false
    )
    private val superuser = UserDto(
        UUID.fromString("00000000-0000-0000-0000-000000000004"),
        "michael.scott@avogador.com",
        "Michael",
        "Scott",
        false,
        true
    )
    private val externalHeader = mapper.writeValueAsString(external)
    private val studentHeader = mapper.writeValueAsString(student)
    private val collaboratorHeader = mapper.writeValueAsString(collaborator)
    private val professorHeader = mapper.writeValueAsString(professor)
    private val superuserHeader = mapper.writeValueAsString(superuser)

    private val practice = Practice(
        UUID.fromString("00000000-0000-0000-0000-000000000001"), "Trial", true, true, ProgrammingLanguage.JAVA, Date(),
        Date.from(
            Instant.now().plus(1, ChronoUnit.DAYS)
        )
    )
    private val exercise = Exercise(practice, "Exercise", "statement", 1, true)
    private val testcase = TestcaseDetailDto(
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        true,
        1,
        "in",
        "out"
    )

    @BeforeEach
    fun setup() {
        val exerciseId = Exercise::class.java.getDeclaredField("id")
        exerciseId.isAccessible = true
        exerciseId.set(exercise, UUID.fromString("00000000-0000-0000-0000-000000000001"))

        val practiceId = Trial::class.java.getDeclaredField("id")
        practiceId.isAccessible = true
        practiceId.set(practice, UUID.fromString("00000000-0000-0000-0000-000000000001"))

        `when`(exerciseService.getExercise(any()))
            .thenReturn(Optional.empty())
        `when`(exerciseService.getExercise(UUID.fromString("00000000-0000-0000-0000-000000000001")))
            .thenReturn(Optional.of(exercise))
        `when`(trialService.getTrialById(any()))
            .thenReturn(Optional.empty())
        `when`(trialService.getTrialById(UUID.fromString("00000000-0000-0000-0000-000000000001")))
            .thenReturn(Optional.of(practice))
        `when`(userCourseService.getUserCourseRole(any(), any()))
            .thenReturn(Optional.of(CourseRole.EXTERNAL))
        `when`(userCourseService.getUserCourseRole(any(), eq(student.id)))
            .thenReturn(Optional.of(CourseRole.STUDENT))
        `when`(userCourseService.getUserCourseRole(any(), eq(collaborator.id)))
            .thenReturn(Optional.of(CourseRole.COLLABORATOR))
        `when`(userCourseService.getUserCourseRole(any(), eq(professor.id)))
            .thenReturn(Optional.of(CourseRole.ADMIN))

    }


    class PrivilegedUserHeadersProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
            Arguments.of(TestcaseControllerTests().collaboratorHeader),
            Arguments.of(TestcaseControllerTests().professorHeader),
            Arguments.of(TestcaseControllerTests().superuserHeader)
        )

    }
    class UnprivilegedUserHeadersProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
            Arguments.of(TestcaseControllerTests().externalHeader),
            Arguments.of(TestcaseControllerTests().studentHeader),
        )

    }

    @Nested
    inner class InsertTestcase {
        @Test
        fun `(404) wrong exercise id`() {
            mvc.put("/public/exercises/00000000-0000-0000-0000-000000000000/testcases") {
                header("User", studentHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(testcase)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UnprivilegedUserHeadersProvider::class)
        fun `(403) student user`(header: String){
            mvc.put("/public/exercises/00000000-0000-0000-0000-000000000001/testcases") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(testcase)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String){
            mvc.put("/public/exercises/00000000-0000-0000-0000-000000000001/testcases") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(testcase)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }


    }

}