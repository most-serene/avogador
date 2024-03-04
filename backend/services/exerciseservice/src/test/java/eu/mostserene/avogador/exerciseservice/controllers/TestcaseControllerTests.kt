package eu.mostserene.avogador.exerciseservice.controllers

import eu.mostserene.avogador.exerciseservice.courses.UserCourseService
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseController
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseDetailDto
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseService
import eu.mostserene.avogador.exerciseservice.trials.TrialService
import eu.mostserene.avogador.exerciseservice.utils.ProfileManager
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
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
import org.springframework.test.web.servlet.*
import java.util.*

@WebMvcTest(TestcaseController::class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestcaseControllerTests : AbstractControllerTests() {
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

    @BeforeEach
    fun setup() {
        `when`(exerciseService.getExercise(any()))
            .thenReturn(Optional.empty())
        `when`(exerciseService.getExercise(visibleExercise.id))
            .thenReturn(Optional.of(visibleExercise))
        `when`(trialService.getTrialById(any()))
            .thenReturn(Optional.empty())
        `when`(trialService.getTrialById(practice.id))
            .thenReturn(Optional.of(practice))
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
        `when`(testcaseService.getTestcasesFromExercise(eq(visibleExercise)))
            .thenReturn(listOf(visibleTestcaseDto, hiddenTestcaseDto))
        `when`(testcaseService.getTestcase(any(), any()))
            .thenReturn(Optional.empty())
        `when`(testcaseService.getTestcase(eq(visibleExercise), eq(visibleTestcaseDto.id)))
            .thenReturn(Optional.of(visibleTestcaseDto))
        `when`(testcaseService.getTestcase(eq(visibleExercise), eq(hiddenTestcaseDto.id)))
            .thenReturn(Optional.of(hiddenTestcaseDto))
        `when`(testcaseService.getSimpleTestcasesFromExercise(any()))
            .thenReturn(listOf())
        `when`(testcaseService.getSimpleTestcasesFromExercise(eq(visibleExercise)))
            .thenReturn(listOf(simpleVisibleTestcase, simpleHiddenTestcase))
        `when`(testcaseService.getSimpleTestcase(any()))
            .thenReturn(Optional.empty())
        `when`(testcaseService.getSimpleTestcase(eq(visibleTestcaseDto.id)))
            .thenReturn(Optional.of(visibleTestcase))
    }

    @Nested
    inner class InsertTestcase {
        @Test
        fun `(404) wrong exercise id`() {
            mvc.put("/public/exercises/00000000-0000-0000-0000-000000000000/testcases") {
                header("User", studentHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(visibleTestcaseDto)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UnprivilegedUserHeadersProvider::class)
        fun `(403) unprivileged user`(header: String) {
            mvc.put("/public/exercises/${visibleExercise.id}/testcases") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(visibleTestcaseDto)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String) {
            mvc.put("/public/exercises/${visibleExercise.id}/testcases") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(visibleTestcaseDto)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class GetTestcasesFromExercise {
        @Test
        fun `(404) wrong exercise id`() {
            mvc.get("/public/exercises/00000000-0000-0000-0000-000000000000/testcases") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @Test
        fun `(403) external user`() {
            mvc.get("/public/exercises/${visibleExercise.id}/testcases") {
                header("User", externalHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(200) student user`() {
            mvc.get("/public/exercises/${visibleExercise.id}/testcases") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
                jsonPath<Collection<TestcaseDetailDto>>("$", hasSize(1))
                jsonPath<String>("$[0].id", `is`(visibleTestcaseDto.id.toString()))
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String) {
            mvc.get("/public/exercises/${visibleExercise.id}/testcases") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
                jsonPath<Collection<TestcaseDetailDto>>("$", hasSize(2))
                jsonPath<String>("$[0].id", `is`(visibleTestcaseDto.id.toString()))
                jsonPath<String>("$[1].id", `is`(hiddenTestcaseDto.id.toString()))
            }
        }
    }

    @Nested
    inner class GetTestcaseById {
        @Test
        fun `(404) wrong exercise id`() {
            mvc.get("/public/exercises/00000000-0000-0000-0000-000000000000/testcases/${visibleTestcaseDto.id}") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @Test
        fun `(403) external user`() {
            mvc.get("/public/exercises/${visibleExercise.id}/testcases/${visibleTestcaseDto.id}") {
                header("User", externalHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(403) external user - hidden testcase`() {
            mvc.get("/public/exercises/${visibleExercise.id}/testcases/${hiddenTestcaseDto.id}") {
                header("User", externalHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(404) wrong testcase id`() {
            mvc.get("/public/exercises/${visibleExercise.id}/testcases/00000000-0000-0000-0000-000000000000") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @Test
        fun `(403) student user - hidden testcase`() {
            mvc.get("/public/exercises/${visibleExercise.id}/testcases/${hiddenTestcaseDto.id}") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(200) student user`() {
            mvc.get("/public/exercises/${visibleExercise.id}/testcases/${visibleTestcaseDto.id}") {
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
            mvc.get("/public/exercises/${visibleExercise.id}/testcases/${visibleTestcaseDto.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user - hidden testcase`(header: String) {
            mvc.get("/public/exercises/${visibleExercise.id}/testcases/${hiddenTestcaseDto.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class UpdateTestcaseOrder {
        @Test
        fun `(404) wrong exercise id`() {
            mvc.patch("/public/exercises/00000000-0000-0000-0000-000000000000/testcases/order") {
                header("User", studentHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(listOf(hiddenTestcaseDto.id, visibleTestcaseDto.id))
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UnprivilegedUserHeadersProvider::class)
        fun `(403) unprivileged user`(header: String) {
            mvc.patch("/public/exercises/${visibleExercise.id}/testcases/order") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(listOf(hiddenTestcaseDto.id, visibleTestcaseDto.id))
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(400) list size mismatch - smaller`() {
            mvc.patch("/public/exercises/${visibleExercise.id}/testcases/order") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(listOf(visibleTestcaseDto.id))
            }.andDo {
                print()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun `(400) list size mismatch - bigger`() {
            mvc.patch("/public/exercises/${visibleExercise.id}/testcases/order") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(
                    listOf(
                        hiddenTestcaseDto.id,
                        visibleTestcaseDto.id,
                        visibleTestcaseDto.id
                    )
                )
            }.andDo {
                print()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun `(400) list with repetitions`() {
            mvc.patch("/public/exercises/${visibleExercise.id}/testcases/order") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(listOf(visibleTestcaseDto.id, visibleTestcaseDto.id))
            }.andDo {
                print()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun `(400) list with wrong ids`() {
            mvc.patch("/public/exercises/${visibleExercise.id}/testcases/order") {
                header("User", professorHeader)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(
                    listOf(
                        UUID.fromString("00000000-0000-0000-0000-000000000000"),
                        visibleTestcaseDto.id
                    )
                )
            }.andDo {
                print()
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String) {
            mvc.patch("/public/exercises/${visibleExercise.id}/testcases/order") {
                header("User", header)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(listOf(hiddenTestcaseDto.id, visibleTestcaseDto.id))
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class DeleteTestcase {
        @Test
        fun `(404) wrong exercise id`() {
            mvc.delete("/public/exercises/00000000-0000-0000-0000-000000000000/testcases/${visibleTestcaseDto.id}") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UnprivilegedUserHeadersProvider::class)
        fun `(403) unprivileged user`(header: String) {
            mvc.delete("/public/exercises/${visibleExercise.id}/testcases/${visibleTestcaseDto.id}") {
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
            mvc.delete("/public/exercises/${visibleExercise.id}/testcases/${visibleTestcaseDto.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }


}