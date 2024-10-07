package eu.mostserene.avogador.exerciseservice.controllers.mocks

import eu.mostserene.avogador.exerciseservice.controllers.AbstractControllerTests
import eu.mostserene.avogador.exerciseservice.practices.PracticeService
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import java.util.*

class PracticeServiceMocks(private val practiceService: PracticeService) : AbstractControllerTests() {

    fun setup() {
        `when`(practiceService.getPractice(any()))
            .thenReturn(Optional.empty())
        `when`(practiceService.getPractice(eq(practice.id)))
            .thenReturn(Optional.of(practice))
        `when`(practiceService.getPractice(eq(hiddenPractice.id)))
            .thenReturn(Optional.of(hiddenPractice))
        `when`(practiceService.getPractice(eq(oldPractice.id)))
            .thenReturn(Optional.of(oldPractice))
    }
}