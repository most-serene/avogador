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
        Mockito.`when`(codingExerciseService.getCodingExercise(visibleExercise.id))
            .thenReturn(Optional.of(visibleExercise))
        Mockito.`when`(codingExerciseService.getCodingExercise(archivedExercise.id))
            .thenReturn(Optional.of(archivedExercise))
        Mockito.`when`(codingExerciseService.getCodingExercise(hiddenExercise.id))
            .thenReturn(Optional.of(hiddenExercise))
        Mockito.`when`(codingExerciseService.updateCodingExercise(visibleExercise))
            .thenReturn(visibleExercise)
    }
}