package eu.mostserene.avogador.exerciseservice.controllers.mocks

import eu.mostserene.avogador.exerciseservice.controllers.AbstractControllerTests
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`

class ExerciseServiceMocks(private val exerciseService: ExerciseService) : AbstractControllerTests() {

    fun setup() {
        `when`(exerciseService.getExercisesFromTrial(eq(practice), eq(true)))
            .thenReturn(listOf(visibleExercise, hiddenExercise))
        `when`(exerciseService.getExercisesFromTrial(eq(practice), eq(false)))
            .thenReturn(listOf(visibleExercise))
    }
}