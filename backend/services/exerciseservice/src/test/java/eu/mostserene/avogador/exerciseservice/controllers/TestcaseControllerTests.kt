package eu.mostserene.avogador.exerciseservice.controllers

import eu.mostserene.avogador.exerciseservice.courses.UserCourseService
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseRepository
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService
import eu.mostserene.avogador.exerciseservice.practices.PracticeRepository
import eu.mostserene.avogador.exerciseservice.practices.PracticeService
import eu.mostserene.avogador.exerciseservice.storage.StorageService
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseController
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseService
import eu.mostserene.avogador.exerciseservice.trials.TrialService
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialService
import eu.mostserene.avogador.exerciseservice.utils.ProfileManager
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.util.*

@WebMvcTest(TestcaseController::class)
@AutoConfigureMockMvc(addFilters = false)
class TestcaseControllerTests {
    @Autowired private lateinit var mvc: MockMvc
    @MockBean private lateinit var buildProperties: BuildProperties
    @MockBean private lateinit var profileManager: ProfileManager
    @MockBean private lateinit var userCourseService: UserCourseService
    @MockBean private lateinit var exerciseService: ExerciseService
    @MockBean private lateinit var testcaseService: TestcaseService
    @MockBean private lateinit var trialService: TrialService

    private val studentHeader =
        "{\"id\":\"00000000-0000-0000-0000-000000000001\", \"email\":\"student@stud.unive.it\", \"givenName\":\"Andy\", \"familyName\":\"Bernard\", \"isProfessor\":false, \"isSuperuser\":false}"

    @Test
    fun `test with spaces in the name`(){
        Mockito.`when`(exerciseService.getExercise(Mockito.any())).thenReturn(Optional.empty());

        mvc.get("/public/exercises"){
            header("User", studentHeader)
        }.andDo {
            print()
        }.andExpect {
            status { isNotFound() }
        }

    }

}