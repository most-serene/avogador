package eu.mostserene.avogador.exerciseservice.controllers

import eu.mostserene.avogador.exerciseservice.abstractexercises.AbstractExerciseDto
import eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises.CodingExerciseService
import eu.mostserene.avogador.exerciseservice.antiplagiarism.AntiPlagiarismService
import eu.mostserene.avogador.exerciseservice.controllers.mocks.ExerciseServiceMocks
import eu.mostserene.avogador.exerciseservice.controllers.mocks.TrialServiceMocks
import eu.mostserene.avogador.exerciseservice.controllers.mocks.UserCourseServiceMocks
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseController
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService
import eu.mostserene.avogador.exerciseservice.storage.StorageService
import eu.mostserene.avogador.exerciseservice.submissions.SubmissionService
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseService
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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get

@WebMvcTest(ExerciseController::class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AbstractExerciseControllerTests : AbstractControllerTests() {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var buildProperties: BuildProperties

    @MockBean
    private lateinit var profileManager: ProfileManager

    @MockBean
    private lateinit var exerciseService: ExerciseService

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
        ExerciseServiceMocks(exerciseService).setup()
        TrialServiceMocks(trialService).setup()
        UserCourseServiceMocks(userCourseService).setup()
    }

    @Nested
    inner class GetExerciseById {
        @Test
        fun `(404) wrong id`() {
            mvc.get("/public/exercises/00000000-0000-0000-0000-000000000000") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(CourseExternalHeadersProvider::class)
        fun `(403) external user`(header: String) {
            mvc.get("/public/exercises/${visibleExercise.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(CourseMemberHeadersProvider::class)
        fun `(200) member user - visible`(header: String) {
            mvc.get("/public/exercises/${visibleExercise.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }

        @Test
        fun `(200) student user - hidden`() {
            mvc.get("/public/exercises/${hiddenExercise.id}") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user - hidden`(header: String) {
            mvc.get("/public/exercises/${hiddenExercise.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class DeleteExercise {
        @Test
        fun `(404) wrong id`() {
            mvc.delete("/public/exercises/00000000-0000-0000-0000-000000000000") {
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
            mvc.delete("/public/exercises/${visibleExercise.id}") {
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
            mvc.delete("/public/exercises/${visibleExercise.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }

        @Test
        fun `(410) archived course`() {
            mvc.delete("/public/exercises/${archivedExercise.id}") {
                header("User", professorHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isGone() }
            }
        }
    }

    @Nested
    inner class GetExercisesFromTrial {
        @Test
        fun `(404) wrong id`() {
            mvc.get("/public/exercises/trials/00000000-0000-0000-0000-000000000000") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }
        
        @ParameterizedTest
        @ArgumentsSource(CourseExternalHeadersProvider::class)
        fun `(403) external`(header: String) {
            mvc.get("/public/exercises/trials/${practice.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(200) student`() {
            mvc.get("/public/exercises/trials/${practice.id}") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status {
                    isOk()
                    jsonPath<Collection<AbstractExerciseDto>>("$", hasSize(1))
                }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged`(header: String) {
            mvc.get("/public/exercises/trials/${practice.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status {
                    isOk()
                    jsonPath<Collection<AbstractExerciseDto>>("$", hasSize(2))
                }
            }
        }
    }
}