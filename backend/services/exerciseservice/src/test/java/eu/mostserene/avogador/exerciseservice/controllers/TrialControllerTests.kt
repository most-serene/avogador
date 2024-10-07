package eu.mostserene.avogador.exerciseservice.controllers

import eu.mostserene.avogador.exerciseservice.amqp.Sender
import eu.mostserene.avogador.exerciseservice.antiplagiarism.AntiPlagiarismService
import eu.mostserene.avogador.exerciseservice.antiplagiarism.similarityreport.SimilarityReportRepository
import eu.mostserene.avogador.exerciseservice.controllers.mocks.UserCourseServiceMocks
import eu.mostserene.avogador.exerciseservice.courses.CourseService
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService
import eu.mostserene.avogador.exerciseservice.storage.StorageService
import eu.mostserene.avogador.exerciseservice.trials.TrialController
import eu.mostserene.avogador.exerciseservice.trials.TrialService
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialService
import eu.mostserene.avogador.exerciseservice.utils.ProfileManager
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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import java.util.*

@WebMvcTest(TrialController::class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TrialControllerTests : AbstractControllerTests() {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var buildProperties: BuildProperties

    @MockBean
    private lateinit var profileManager: ProfileManager

    @MockBean
    private lateinit var exerciseService: ExerciseService

    @MockBean
    private lateinit var userTrialService: UserTrialService

    @MockBean
    private lateinit var userCourseService: UserCourseService

    @MockBean
    private lateinit var courseService: CourseService

    @MockBean
    private lateinit var trialService: TrialService

    @MockBean
    private lateinit var storageService: StorageService

    @MockBean
    private lateinit var antiPlagiarismService: AntiPlagiarismService

    @MockBean
    private lateinit var similarityReportRepository: SimilarityReportRepository

    @MockBean
    private lateinit var sender: Sender

    @BeforeEach
    fun setup() {
        UserCourseServiceMocks(userCourseService).setup()
        `when`(trialService.getTrialById(any()))
            .thenReturn(Optional.empty())
        `when`(trialService.getTrialById(eq(practice.id)))
            .thenReturn(Optional.of(practice))
        `when`(trialService.getTrialById(eq(practiceInArchivedCourse.id)))
            .thenReturn(Optional.of(practiceInArchivedCourse))
        `when`(userCourseService.getUserCourseRoleDetail(any(), any()))
            .thenReturn(Optional.empty())
        `when`(userCourseService.getUserCourseRoleDetail(eq(course.id), eq(external.id)))
            .thenReturn(Optional.of(courseDetailDtoExternal))
        `when`(userCourseService.getUserCourseRoleDetail(eq(course.id), eq(superuser.id)))
            .thenReturn(Optional.of(courseDetailDtoExternal))
        `when`(userCourseService.getUserCourseRoleDetail(eq(course.id), eq(student.id)))
            .thenReturn(Optional.of(courseDetailDtoStudent))
        `when`(userCourseService.getUserCourseRoleDetail(eq(course.id), eq(collaborator.id)))
            .thenReturn(Optional.of(courseDetailDtoCollaborator))
        `when`(userCourseService.getUserCourseRoleDetail(eq(course.id), eq(professor.id)))
            .thenReturn(Optional.of(courseDetailDtoAdmin))
        `when`(userCourseService.getUserCourseRoleDetail(eq(archivedCourse.id), eq(professor.id)))
            .thenReturn(Optional.of(archivedCourseDetailDtoAdmin))
    }


    @Nested
    inner class GetTrialById {
        @Test
        fun `(404) wrong id`() {
            mvc.get("/public/trials/${emptyId}") {
                header("User", externalHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(CourseExternalHeadersProvider::class)
        fun `(403) external user`(header: String) {
            mvc.get("/public/trials/${practice.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(CourseMemberHeadersProvider::class)
        fun `(200) member user`(header: String) {
            mvc.get("/public/trials/${practice.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class GetTrialsFromCourse {
        @Test
        fun `(403) wrong id`() {
            mvc.get("/public/trials/courses/${emptyId}") {
                header("User", externalHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(CourseExternalHeadersProvider::class)
        fun `(403) external user`(header: String) {
            mvc.get("/public/trials/courses/${emptyId}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() } // TODO: check trial count
            }
        }

        @ParameterizedTest
        @ArgumentsSource(CourseMemberHeadersProvider::class)
        fun `(200) member user`(header: String) {
            mvc.get("/public/trials/courses/${course.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() } //TODO: check trial count
            }
        }
    }

    @Nested
    inner class DeleteTrial {
        @Test
        fun `(404) wrong id`() {
            mvc.delete("/public/trials/${emptyId}") {
                header("User", externalHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UnprivilegedUserHeadersProvider::class)
        fun `(403) unprivileged user`(header: String) {
            mvc.delete("/public/trials/${practice.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(410) archived course`() {
            mvc.delete("/public/trials/${practiceInArchivedCourse.id}") {
                header("User", professorHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isGone() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged user`(header: String) {
            mvc.delete("/public/trials/${practice.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }

    }


}