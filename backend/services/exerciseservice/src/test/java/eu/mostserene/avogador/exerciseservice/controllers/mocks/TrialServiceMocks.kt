package eu.mostserene.avogador.exerciseservice.controllers.mocks

import eu.mostserene.avogador.exerciseservice.controllers.AbstractControllerTests
import eu.mostserene.avogador.exerciseservice.trials.TrialService
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import java.util.*

class TrialServiceMocks(private val trialService: TrialService) : AbstractControllerTests() {

    fun setup() {
        `when`(trialService.getTrialById(any()))
            .thenReturn(Optional.empty())
        `when`(trialService.getTrialById(eq(practice.id)))
            .thenReturn(Optional.of(practice))
        `when`(trialService.getTrialById(eq(practiceInArchivedCourse.id)))
            .thenReturn(Optional.of(practiceInArchivedCourse))
    }

}