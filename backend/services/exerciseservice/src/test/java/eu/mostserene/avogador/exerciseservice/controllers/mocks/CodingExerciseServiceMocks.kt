package eu.mostserene.avogador.exerciseservice.controllers.mocks

import eu.mostserene.avogador.exerciseservice.controllers.AbstractControllerTests
import eu.mostserene.avogador.exerciseservice.exercises.codingexercises.CodingExerciseService
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.util.*

class CodingExerciseServiceMocks(private val codingExerciseService: CodingExerciseService) : AbstractControllerTests() {

    fun setup() {
        Mockito.`when`(codingExerciseService.getCodingExercise(ArgumentMatchers.any()))
            .thenReturn(Optional.empty())
        Mockito.`when`(codingExerciseService.getCodingExercise(visibleCodingExercise.id))
            .thenReturn(Optional.of(visibleCodingExercise))
        Mockito.`when`(codingExerciseService.getCodingExercise(archivedCodingExercise.id))
            .thenReturn(Optional.of(archivedCodingExercise))
        Mockito.`when`(codingExerciseService.getCodingExercise(hiddenCodingExercise.id))
            .thenReturn(Optional.of(hiddenCodingExercise))
        Mockito.`when`(codingExerciseService.updateCodingExercise(visibleCodingExercise))
            .thenReturn(visibleCodingExercise)
    }
}