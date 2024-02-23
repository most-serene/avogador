package eu.mostserene.avogador.exerciseservice.controllers

import eu.mostserene.avogador.exerciseservice.courses.UserCourseService
import eu.mostserene.avogador.exerciseservice.exercises.Exercise
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService
import eu.mostserene.avogador.exerciseservice.practices.Practice
import eu.mostserene.avogador.exerciseservice.practices.PracticeController
import eu.mostserene.avogador.exerciseservice.practices.PracticeRepository
import eu.mostserene.avogador.exerciseservice.practices.PracticeService
import eu.mostserene.avogador.exerciseservice.storage.StorageService
import eu.mostserene.avogador.exerciseservice.trials.Trial
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialService
import eu.mostserene.avogador.exerciseservice.utils.ProfileManager
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
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
import kotlin.test.assertNotEquals

@WebMvcTest(PracticeController::class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PracticeControllerTests : AbstractControllerTests() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var practiceService: PracticeService

    @MockBean
    private lateinit var practiceRepository: PracticeRepository

    @MockBean
    private lateinit var userCourseService: UserCourseService

    @MockBean
    private lateinit var buildProperties: BuildProperties

    @MockBean
    private lateinit var profileManager: ProfileManager

    @MockBean
    private lateinit var storageService: StorageService

    @MockBean
    private lateinit var exerciseService: ExerciseService

    @MockBean
    private lateinit var userTrialService: UserTrialService

    @BeforeEach
    fun setup() {
        `when`(practiceService.getPractice(any()))
                .thenReturn(Optional.empty())
        `when`(practiceService.getPractice(eq(practice.id)))
                .thenReturn(Optional.of(practice))
        `when`(practiceService.getPractice(eq(hiddenPractice.id)))
                .thenReturn(Optional.of(hiddenPractice))
        `when`(practiceService.getPractice(eq(oldPractice.id)))
                .thenReturn(Optional.of(oldPractice))
        `when`(userCourseService.getUserCourseRoleDetail(any(), any()))
                .thenReturn(Optional.empty())
        `when`(userCourseService.getUserCourseRoleDetail(any(), eq(external.id)))
                .thenReturn(Optional.of(courseDetailDtoExternal))
        `when`(userCourseService.getUserCourseRoleDetail(any(), eq(superuser.id)))
                .thenReturn(Optional.of(courseDetailDtoExternal))
        `when`(userCourseService.getUserCourseRoleDetail(any(), eq(student.id)))
                .thenReturn(Optional.of(courseDetailDtoStudent))
        `when`(userCourseService.getUserCourseRoleDetail(any(), eq(collaborator.id)))
                .thenReturn(Optional.of(courseDetailDtoCollaborator))
        `when`(userCourseService.getUserCourseRoleDetail(any(), eq(professor.id)))
                .thenReturn(Optional.of(courseDetailDtoAdmin))
        `when`(exerciseService.getExercisesFromTrial(eq(practice), eq(true)))
                .thenReturn(listOf(visibleExercise, hiddenExercise))
        `when`(exerciseService.getExercisesFromTrial(eq(practice), eq(false)))
                .thenReturn(listOf(visibleExercise))
        `when`(userTrialService.joinTrial(eq(student), eq(practice)))
                .thenReturn(studentPractice)
    }

    @Nested
    inner class GetPractice {
        @Test
        fun `(404) wrong practice id`() {
            mvc.get("/public/trials/practices/00000000-0000-0000-0000-000000000000") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @Test
        fun `(403) external user`() {
            mvc.get("/public/trials/practices/${practice.id}") {
                header("User", externalHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UnprivilegedUserHeadersProvider::class)
        fun `(403) unprivileged user - hidden practice`(header: String) {
            mvc.get("/public/trials/practices/${hiddenPractice.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user - hidden practice`(header: String) {
            mvc.get("/public/trials/practices/${hiddenPractice.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }

        @Test
        fun `(200) student user`() {
            mvc.get("/public/trials/practices/${practice.id}") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String) {
            mvc.get("/public/trials/practices/${practice.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class CreatePractice {
        @ParameterizedTest
        @ArgumentsSource(UnprivilegedUserHeadersProvider::class)
        fun `(403) unprivileged user`(header: String) {
            mvc.post("/public/trials/practices") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(practice)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(400) starts in the past`() {
            mvc.post("/public/trials/practices") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(practiceStartingPast)
            }.andDo {
                print()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun `(400) ends before start`() {
            mvc.post("/public/trials/practices") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(practiceEndingBeforeStart)
            }.andDo {
                print()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String) {
            mvc.post("/public/trials/practices") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(practice)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class UpdatePractice {
        @Test
        fun `(404) wrong practice id`() {
            mvc.put("/public/trials/practices/00000000-0000-0000-0000-000000000000") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(practice)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UnprivilegedUserHeadersProvider::class)
        fun `(403) unprivileged user`(header: String) {
            mvc.put("/public/trials/practices/${practice.id}") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(practice)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(400) starts in the past`() {
            mvc.put("/public/trials/practices/${practice.id}") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(practiceStartingPast)
            }.andDo {
                print()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun `(400) ends before start`() {
            mvc.put("/public/trials/practices/${practice.id}") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(practiceEndingBeforeStart)
            }.andDo {
                print()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun `(400) different courseId`() {
            val practiceWithDifferentCourseId = Practice(
                    UUID.fromString("00000000-0000-0000-0000-000000000000"),
                    practice.name,
                    practice.isVisible,
                    practice.isPublic,
                    practice.language,
                    practice.startTimestamp,
                    practice.deadline
            )
            val practiceId = Trial::class.java.getDeclaredField("id")
            practiceId.isAccessible = true
            practiceId.set(practiceWithDifferentCourseId, practice.id)

            mvc.put("/public/trials/practices/${practice.id}") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(practiceWithDifferentCourseId)
            }.andDo {
                print()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String) {
            mvc.put("/public/trials/practices/${practice.id}") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(practice)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }

        @Test
        fun `(200) only change name`() {
            val practiceWithNewName = Practice(
                    oldPractice.courseId,
                    "Trial with new name",
                    oldPractice.isVisible,
                    oldPractice.isPublic,
                    oldPractice.language,
                    oldPractice.startTimestamp,
                    oldPractice.deadline
            )
            val practiceId = Trial::class.java.getDeclaredField("id")
            practiceId.isAccessible = true
            practiceId.set(practiceWithNewName, oldPractice.id)

            mvc.put("/public/trials/practices/${oldPractice.id}") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(practiceWithNewName)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }

        @Test
        fun `(200) only change finishDate`() {
            val practiceWithNewFinishDate = Practice(
                    oldPractice.courseId,
                    oldPractice.name,
                    oldPractice.isVisible,
                    oldPractice.isPublic,
                    oldPractice.language,
                    oldPractice.startTimestamp,
                    Date.from(Instant.now().plus(13, ChronoUnit.DAYS))
            )
            val practiceId = Trial::class.java.getDeclaredField("id")
            practiceId.isAccessible = true
            practiceId.set(practiceWithNewFinishDate, oldPractice.id)


            mvc.put("/public/trials/practices/${oldPractice.id}") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(practiceWithNewFinishDate)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class GetExercisesFromPractice {
        @Test
        fun `(404) wrong practice id`() {
            mvc.get("/public/trials/practices/00000000-0000-0000-0000-000000000000/exercises") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @Test
        fun `(403) external user`() {
            mvc.get("/public/trials/practices/${practice.id}/exercises") {
                header("User", externalHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(200) student user`() {
            mvc.get("/public/trials/practices/${practice.id}/exercises") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
                jsonPath<Collection<Exercise>>("$", hasSize(1))
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String) {
            mvc.get("/public/trials/practices/${practice.id}/exercises") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
                jsonPath<Collection<Exercise>>("$", hasSize(2))
            }
        }
    }

    @Nested
    inner class JoinPractice {
        @Test
        fun `(404) wrong practice id`() {
            mvc.put("/public/trials/practices/00000000-0000-0000-0000-000000000000/join") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @Test
        fun `(403) external user`() {
            mvc.put("/public/trials/practices/${practice.id}/join") {
                header("User", externalHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(200 - response) student user`() {
            val result = mvc.put("/public/trials/practices/${practice.id}/join") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }.andReturn()

            assertNotEquals("", result.response.contentAsString)
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200 - empty) privileged user`(header: String) {
            val result = mvc.put("/public/trials/practices/${practice.id}/join") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }.andReturn()

            assertEquals("", result.response.contentAsString)
        }

    }

}