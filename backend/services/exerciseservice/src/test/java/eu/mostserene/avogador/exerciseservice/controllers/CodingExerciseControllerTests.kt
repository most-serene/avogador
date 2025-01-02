package eu.mostserene.avogador.exerciseservice.controllers

import eu.mostserene.avogador.exerciseservice.antiplagiarism.AntiPlagiarismService
import eu.mostserene.avogador.exerciseservice.controllers.mocks.*
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService
import eu.mostserene.avogador.exerciseservice.exercises.codingexercises.CodingExercise
import eu.mostserene.avogador.exerciseservice.exercises.codingexercises.CodingExerciseController
import eu.mostserene.avogador.exerciseservice.exercises.codingexercises.CodingExerciseDto
import eu.mostserene.avogador.exerciseservice.exercises.codingexercises.CodingExerciseService
import eu.mostserene.avogador.exerciseservice.practices.Practice
import eu.mostserene.avogador.exerciseservice.storage.StorageService
import eu.mostserene.avogador.exerciseservice.strox.Strox
import eu.mostserene.avogador.exerciseservice.strox.StroxCell
import eu.mostserene.avogador.exerciseservice.submissions.SubmissionService
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseService
import eu.mostserene.avogador.exerciseservice.trials.ProgrammingLanguage
import eu.mostserene.avogador.exerciseservice.trials.TrialService
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialService
import eu.mostserene.avogador.exerciseservice.utils.ProfileManager
import org.hamcrest.Matchers.hasSize
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

@WebMvcTest(CodingExerciseController::class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CodingExerciseControllerTests : AbstractControllerTests() {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var buildProperties: BuildProperties

    @MockBean
    private lateinit var profileManager: ProfileManager

    @MockBean
    private lateinit var codingExerciseService: CodingExerciseService

    @MockBean
    private lateinit var userTrialService: UserTrialService

    @MockBean
    private lateinit var userCourseService: UserCourseService

    @MockBean
    private lateinit var trialService: TrialService

    @MockBean
    private lateinit var storageService: StorageService

    @MockBean
    private lateinit var submissionService: SubmissionService

    @MockBean
    private lateinit var testcaseService: TestcaseService

    @MockBean
    private lateinit var antiPlagiarismService: AntiPlagiarismService

    @BeforeEach
    fun setup() {
        AntiPlagiarismServiceMocks(antiPlagiarismService).setup()
        CodingExerciseServiceMocks(codingExerciseService).setup()
        StorageServiceMocks(storageService).setup()
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
            val tempExercise = CodingExercise(tempPractice, "Exercise1", "statement", true, 1, ProgrammingLanguage.JAVA)
            val tempExerciseDto = CodingExerciseDto(tempExercise)

            mvc.post("/public/exercises/coding") {
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
            mvc.post("/public/exercises/coding") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(CodingExerciseDto(visibleCodingExercise))
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(410) archived course`() {
            mvc.post("/public/exercises/coding") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(CodingExerciseDto(archivedCodingExercise))
            }.andDo {
                print()
            }.andExpect {
                status { isGone() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String) {
            mvc.post("/public/exercises/coding") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(CodingExerciseDto(visibleCodingExercise))
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class CreateExerciseTemplate {
        @Test
        fun `(404) wrong id`() {
            mvc.post("/public/exercises/coding/${emptyId}/template") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(Strox())
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UnprivilegedUserHeadersProvider::class)
        fun `(403) unprivileged user`(header: String) {
            mvc.post("/public/exercises/coding/${visibleCodingExercise.id}/template") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(Strox())
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(410) archived course`() {
            mvc.post("/public/exercises/coding/${archivedCodingExercise.id}/template") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(Strox())
            }.andDo {
                print()
            }.andExpect {
                status { isGone() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String) {
            mvc.post("/public/exercises/coding/${visibleCodingExercise.id}/template") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(Strox())
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
            mvc.put("/public/exercises/coding/${emptyId}") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(CodingExerciseDto(visibleCodingExercise))
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UnprivilegedUserHeadersProvider::class)
        fun `(403) unprivileged user`(header: String) {
            mvc.put("/public/exercises/coding/${visibleCodingExercise.id}") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(CodingExerciseDto(visibleCodingExercise))
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(410) archived course`() {
            mvc.put("/public/exercises/coding/${archivedCodingExercise.id}") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(CodingExerciseDto(archivedCodingExercise))
            }.andDo {
                print()
            }.andExpect {
                status { isGone() }
            }
        }

        @Test
        fun `(400) id mismatch`() {
            mvc.put("/public/exercises/coding/${visibleCodingExercise.id}") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(CodingExerciseDto(hiddenCodingExercise))
            }.andDo {
                print()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String) {
            mvc.put("/public/exercises/coding/${visibleCodingExercise.id}") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(CodingExerciseDto(visibleCodingExercise))
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class GetExerciseTemplate {
        @Test
        fun `(404) wrong id`() {
            mvc.get("/public/exercises/coding/${emptyId}/template") {
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
            mvc.get("/public/exercises/coding/${visibleCodingExercise.id}/template") {
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
            mvc.get("/public/exercises/coding/${hiddenCodingExercise.id}/template") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(200) student user`() {
            mvc.get("/public/exercises/coding/${visibleCodingExercise.id}/template") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
                jsonPath<Collection<StroxCell>>("$.cells", hasSize(2))
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String) {
            mvc.get("/public/exercises/coding/${visibleCodingExercise.id}/template") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
                jsonPath<Collection<StroxCell>>("$.cells", hasSize(3))
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user - hidden exercise`(header: String) {
            mvc.get("/public/exercises/coding/${hiddenCodingExercise.id}/template") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
                jsonPath<Collection<StroxCell>>("$.cells", hasSize(3))
            }
        }
    }

    @Nested
    inner class GetSimilarityReportPresence {
        @Test
        fun `(404) wrong id`() {
            mvc.get("/public/exercises/coding/${emptyId}/similarity-report-presence") {
                header("User", professorHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UnprivilegedUserHeadersProvider::class)
        fun `(403) unprivileged user`(header: String) {
            mvc.get("/public/exercises/coding/${visibleCodingExercise.id}/similarity-report-presence") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String) {
            mvc.get("/public/exercises/coding/${visibleCodingExercise.id}/similarity-report-presence") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class GetSimilarityReport {
        @Test
        fun `(404) wrong id`() {
            mvc.get("/public/exercises/coding/${emptyId}/similarity-report") {
                header("User", professorHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UnprivilegedUserHeadersProvider::class)
        fun `(403) unprivileged user`(header: String) {
            mvc.get("/public/exercises/coding/${visibleCodingExercise.id}/similarity-report") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(404) empty report`() {
            mvc.get("/public/exercises/coding/${hiddenCodingExercise.id}/similarity-report") {
                header("User", professorHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String) {
            mvc.get("/public/exercises/coding/${visibleCodingExercise.id}/similarity-report") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class ExportExercise {
        @Test
        fun `(404) wrong id`() {
            mvc.get("/public/exercises/coding/${emptyId}/export") {
                header("User", professorHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UnprivilegedUserHeadersProvider::class)
        fun `(403) unprivileged user`(header: String) {
            mvc.get("/public/exercises/coding/${visibleCodingExercise.id}/export") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(500) missing template`() {
            mvc.get("/public/exercises/coding/${archivedCodingExercise.id}/export") {
                header("User", professorHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isInternalServerError() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String) {
            mvc.get("/public/exercises/coding/${visibleCodingExercise.id}/export") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }

}