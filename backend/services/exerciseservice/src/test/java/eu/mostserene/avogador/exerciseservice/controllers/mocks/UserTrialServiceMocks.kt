package eu.mostserene.avogador.exerciseservice.controllers.mocks

import eu.mostserene.avogador.exerciseservice.controllers.AbstractControllerTests
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialService
import org.mockito.Mockito.eq
import org.mockito.Mockito.`when`

class UserTrialServiceMocks(private val userTrialService: UserTrialService) : AbstractControllerTests() {

    fun setup() {
        `when`(userTrialService.joinTrial(eq(student), eq(practice)))
            .thenReturn(studentPractice)
    }
}