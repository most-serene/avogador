package eu.mostserene.avogador.exerciseservice.controllers

import eu.mostserene.avogador.exerciseservice.controllers.mocks.MultipleChoiceServiceMocks
import eu.mostserene.avogador.exerciseservice.controllers.mocks.TrialServiceMocks
import eu.mostserene.avogador.exerciseservice.controllers.mocks.UserCourseServiceMocks
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService
import eu.mostserene.avogador.exerciseservice.exercises.multiplechoiceexercises.*
import eu.mostserene.avogador.exerciseservice.practices.Practice
import eu.mostserene.avogador.exerciseservice.trials.ProgrammingLanguage
import eu.mostserene.avogador.exerciseservice.trials.TrialService
import eu.mostserene.avogador.exerciseservice.utils.ProfileManager
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
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

@WebMvcTest(MultipleChoiceExerciseController::class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MultipleChoiceExerciseControllerTests : AbstractControllerTests() {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var buildProperties: BuildProperties

    @MockBean
    private lateinit var profileManager: ProfileManager

    @MockBean
    private lateinit var exerciseService: ExerciseService

    @MockBean
    private lateinit var multipleChoiceService: MultipleChoiceService

    @MockBean
    private lateinit var userCourseService: UserCourseService

    @MockBean
    private lateinit var trialService: TrialService

    @BeforeEach
    fun setup() {
        MultipleChoiceServiceMocks(multipleChoiceService).setup()
        TrialServiceMocks(trialService).setup()
        UserCourseServiceMocks(userCourseService).setup()
    }

    @Nested
    inner class CreateExercise {
        @Test
        fun `(404) wrong trial id`() {
            val tempPractice = Practice(
                courseId,
                "Trial",
                true,
                true,
                ProgrammingLanguage.JAVA,
                Date.from(Instant.now().plus(1, ChronoUnit.MINUTES)),
                Date.from(Instant.now().plus(1, ChronoUnit.DAYS))
            )
            val tempExercise =
                MultipleChoiceExercise(tempPractice, "MCE0", "statement", true, true, 1.0, 0.0, false, false)
            val tempExerciseDto = MultipleChoiceExerciseDto(tempExercise)

            mvc.post("/public/exercises/multichoice") {
                header("User", collaboratorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(tempExerciseDto)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UnprivilegedUserHeadersProvider::class)
        fun `(403) unprivileged user`(header: String) {
            mvc.post("/public/exercises/multichoice") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(MultipleChoiceExerciseDto(visibleMultipleChoiceExercise))
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(410) archived course`() {
            mvc.post("/public/exercises/multichoice") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(MultipleChoiceExerciseDto(archivedMultipleChoiceExercise))
            }.andDo {
                print()
            }.andExpect {
                status { isGone() }
            }
        }

        @Test
        fun `(400) positive wrong points`() {
            val tempExercise =
                MultipleChoiceExercise(practice, "MCE0", "statement", true, true, 1.0, 0.1, false, false)
            val tempExerciseDto = MultipleChoiceExerciseDto(tempExercise)

            mvc.post("/public/exercises/multichoice") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(tempExerciseDto)
            }.andDo {
                print()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun `(400) negative correct points`() {
            val tempExercise =
                MultipleChoiceExercise(practice, "MCE0", "statement", true, true, -1.0, -10.0, false, false)
            val tempExerciseDto = MultipleChoiceExerciseDto(tempExercise)

            mvc.post("/public/exercises/multichoice") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(tempExerciseDto)
            }.andDo {
                print()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun `(400) no correct options`() {
            val exerciseDto = MultipleChoiceExerciseDto(hiddenMultipleChoiceExercise)
            exerciseDto.options = listOf()

            mvc.post("/public/exercises/multichoice") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(exerciseDto)
            }.andDo {
                print()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun `(400) too many correct options`() {
            val exerciseDto = MultipleChoiceExerciseDto(hiddenMultipleChoiceExercise)
            exerciseDto.options = listOf(MultipleChoiceOptionDto(option1), MultipleChoiceOptionDto(option2))

            mvc.post("/public/exercises/multichoice") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(exerciseDto)
            }.andDo {
                print()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String) {
            val exerciseDto = MultipleChoiceExerciseDto(visibleMultipleChoiceExercise)
            exerciseDto.options = listOf(
                MultipleChoiceOptionDto(option1),
                MultipleChoiceOptionDto(option2),
                MultipleChoiceOptionDto(option3),
                MultipleChoiceOptionDto(option4)
            )

            mvc.post("/public/exercises/multichoice") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(exerciseDto)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class UpdateExercise {
        @Test
        fun `(404) wrong id`() {
            mvc.put("/public/exercises/multichoice/${emptyId}") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(MultipleChoiceExerciseDto(visibleMultipleChoiceExercise))
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UnprivilegedUserHeadersProvider::class)
        fun `(403) unprivileged user`(header: String) {
            mvc.put("/public/exercises/multichoice/${emptyId}") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(MultipleChoiceExerciseDto(visibleMultipleChoiceExercise))
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(410) archived course`() {
            mvc.put("/public/exercises/multichoice/${archivedMultipleChoiceExercise.id}") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(MultipleChoiceExerciseDto(archivedMultipleChoiceExercise))
            }.andDo {
                print()
            }.andExpect {
                status { isGone() }
            }
        }

        @Test
        fun `(400) id mismatch`() {
            mvc.put("/public/exercises/multichoice/${visibleMultipleChoiceExercise.id}") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(MultipleChoiceExerciseDto(hiddenMultipleChoiceExercise))
            }.andDo {
                print()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun `(400) no correct options`() {
            val exerciseDto = MultipleChoiceExerciseDto(hiddenMultipleChoiceExercise)
            exerciseDto.options = listOf()

            mvc.put("/public/exercises/multichoice/${hiddenMultipleChoiceExercise.id}") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(exerciseDto)
            }.andDo {
                print()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun `(400) too many correct options`() {
            val exerciseDto = MultipleChoiceExerciseDto(hiddenMultipleChoiceExercise)
            exerciseDto.options = listOf(MultipleChoiceOptionDto(option1), MultipleChoiceOptionDto(option2))

            mvc.put("/public/exercises/multichoice/${hiddenMultipleChoiceExercise.id}") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(exerciseDto)
            }.andDo {
                print()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String) {
            val tempExercise = MultipleChoiceExerciseDto(visibleMultipleChoiceExercise)
            tempExercise.options = listOf(MultipleChoiceOptionDto(option1), MultipleChoiceOptionDto(option2))

            mvc.put("/public/exercises/multichoice/${visibleMultipleChoiceExercise.id}") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(tempExercise)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class GetExerciseOptions {
        @Test
        fun `(404) wrong id`() {
            mvc.get("/public/exercises/multichoice/${emptyId}/options") {
                header("User", professorHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(CourseExternalHeadersProvider::class)
        fun `(403) external user`(header: String) {
            mvc.get("/public/exercises/multichoice/${visibleMultipleChoiceExercise.id}/options") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UnprivilegedUserHeadersProvider::class)
        fun `(403) unprivileged user - hidden exercise`(header: String) {
            mvc.get("/public/exercises/multichoice/${hiddenMultipleChoiceExercise.id}/options") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(CourseMemberHeadersProvider::class)
        fun `(200) member user - visible exercise`(header: String) {
            mvc.get("/public/exercises/multichoice/${visibleMultipleChoiceExercise.id}/options") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user - hidden exercise`(header: String) {
            mvc.get("/public/exercises/multichoice/${hiddenMultipleChoiceExercise.id}/options") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }

}