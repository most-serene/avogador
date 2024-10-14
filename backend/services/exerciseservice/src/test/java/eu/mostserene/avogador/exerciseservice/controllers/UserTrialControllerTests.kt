package eu.mostserene.avogador.exerciseservice.controllers

import eu.mostserene.avogador.exerciseservice.controllers.mocks.TrialServiceMocks
import eu.mostserene.avogador.exerciseservice.controllers.mocks.UserCourseServiceMocks
import eu.mostserene.avogador.exerciseservice.controllers.mocks.UserTrialServiceMocks
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService
import eu.mostserene.avogador.exerciseservice.trials.TrialService
import eu.mostserene.avogador.exerciseservice.users.UserService
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialController
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialService
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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


@WebMvcTest(UserTrialController::class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserTrialControllerTests : AbstractControllerTests() {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var buildProperties: BuildProperties

    @MockBean
    private lateinit var profileManager: ProfileManager

    @MockBean
    private lateinit var userTrialService: UserTrialService

    @MockBean
    private lateinit var userService: UserService

    @MockBean
    private lateinit var userCourseService: UserCourseService

    @MockBean
    private lateinit var trialService: TrialService

    @BeforeEach
    fun setup() {
        TrialServiceMocks(trialService).setup()
        UserCourseServiceMocks(userCourseService).setup()
        UserTrialServiceMocks(userTrialService).setup()
    }

    @Nested
    inner class GetUsersFromTrial {
        @Test
        fun `(404) wrong id`() {
            mvc.get("/public/trials/00000000-0000-0000-0000-000000000000/users") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isNotFound() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UnprivilegedUserHeadersProvider::class)
        fun `(403) unprivileged`(header: String) {
            mvc.get("/public/trials/${practice.id}/users") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @ParameterizedTest
        @ArgumentsSource(PrivilegedUserHeadersProvider::class)
        fun `(200) privileged`(header: String) {
            mvc.get("/public/trials/${practice.id}/users") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class GetUserTrial {
        @Test
        fun `(404) wrong id`() {
            mvc.get("/public/trials/00000000-0000-0000-0000-000000000000/users/${student.id}") {
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
            mvc.get("/public/trials/${practice.id}/users/${student.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(403) not getting self`() {
            mvc.get("/public/trials/${practice.id}/users/${collaborator.id}") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(200) student self`() {
            val result = mvc.get("/public/trials/${practice.id}/users/${student.id}") {
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
        fun `(200) privileged`(header: String) {
            val result = mvc.get("/public/trials/${practice.id}/users/${collaborator.id}") {
                header("User", header)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }.andReturn()

            assertEquals("", result.response.contentAsString)
        }
    }

    @Nested
    inner class GetTrialsFromUser {
        @Test
        fun `(403) different user`() {
            mvc.get("/public/trials/users/${student.id}") {
                header("User", collaboratorHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        fun `(200) same user`() {
            mvc.get("/public/trials/users/${student.id}") {
                header("User", studentHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }

        @Test
        fun `(200) superuser`() {
            mvc.get("/public/trials/users/${student.id}") {
                header("User", superuserHeader)
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
            }
        }
    }
}