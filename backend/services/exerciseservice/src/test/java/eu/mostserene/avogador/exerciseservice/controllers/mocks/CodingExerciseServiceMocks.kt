package eu.mostserene.avogador.exerciseservice.controllers.mocks

import eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises.CodingExerciseService
import eu.mostserene.avogador.exerciseservice.controllers.AbstractControllerTests
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.util.*

class CodingExerciseServiceMocks(private val codingExerciseService: CodingExerciseService) : AbstractControllerTests() {

    fun setup() {
        Mockito.`when`(codingExerciseService.getCodingExercise(ArgumentMatchers.any()))
            .thenReturn(Optional.empty())
        Mockito.`when`(codingExerciseService.getCodingExercise(visibleExercise.id))
            .thenReturn(Optional.of(visibleExercise))
    }
}