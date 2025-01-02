package eu.mostserene.avogador.exerciseservice.controllers.mocks

import eu.mostserene.avogador.exerciseservice.controllers.AbstractControllerTests
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import java.util.*

class ExerciseServiceMocks(private val exerciseService: ExerciseService) : AbstractControllerTests() {

    fun setup() {
        `when`(exerciseService.getExercisesFromTrial(eq(practice), eq(true)))
            .thenReturn(listOf(visibleCodingExercise, hiddenCodingExercise))
        `when`(exerciseService.getExercisesFromTrial(eq(practice), eq(false)))
            .thenReturn(listOf(visibleCodingExercise))
        `when`(exerciseService.getExercise(eq(visibleCodingExercise.id)))
            .thenReturn(Optional.of(visibleCodingExercise))
        `when`(exerciseService.getExercise(eq(hiddenCodingExercise.id)))
            .thenReturn(Optional.of(hiddenCodingExercise))
        `when`(exerciseService.getExercise(eq(archivedCodingExercise.id)))
            .thenReturn(Optional.of(archivedCodingExercise))
    }
}