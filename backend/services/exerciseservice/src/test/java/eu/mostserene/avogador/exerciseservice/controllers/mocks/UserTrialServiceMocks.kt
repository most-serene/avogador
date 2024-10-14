package eu.mostserene.avogador.exerciseservice.controllers.mocks

import eu.mostserene.avogador.exerciseservice.controllers.AbstractControllerTests
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrialService
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.eq
import org.mockito.Mockito.`when`
import java.util.*

class UserTrialServiceMocks(private val userTrialService: UserTrialService) : AbstractControllerTests() {

    fun setup() {
        `when`(userTrialService.joinTrial(eq(student), eq(practice)))
            .thenReturn(studentPractice)
        `when`(userTrialService.getUserTrial(any(), any()))
            .thenReturn(Optional.empty())
        `when`(userTrialService.getUserTrial(eq(practice), eq(student)))
            .thenReturn(Optional.of(studentPractice))
        `when`(userTrialService.getTrialsFromUser(any()))
            .thenReturn(listOf())
        `when`(userTrialService.getTrialsFromUser(eq(student)))
            .thenReturn(listOf(studentPractice))
    }
}