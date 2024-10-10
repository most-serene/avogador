package eu.mostserene.avogador.exerciseservice.controllers.mocks

import eu.mostserene.avogador.exerciseservice.controllers.AbstractControllerTests
import eu.mostserene.avogador.exerciseservice.exercises.ExerciseService
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import java.util.*

class ExerciseServiceMocks(private val exerciseService: ExerciseService) : AbstractControllerTests() {

    fun setup() {
        `when`(exerciseService.getExercisesFromTrial(eq(practice), eq(true)))
            .thenReturn(listOf(visibleExercise, hiddenExercise))
        `when`(exerciseService.getExercisesFromTrial(eq(practice), eq(false)))
            .thenReturn(listOf(visibleExercise))
        `when`(exerciseService.getExercise(eq(visibleExercise.id)))
            .thenReturn(Optional.of(visibleExercise))
        `when`(exerciseService.getExercise(eq(hiddenExercise.id)))
            .thenReturn(Optional.of(hiddenExercise))
        `when`(exerciseService.getExercise(eq(archivedExercise.id)))
            .thenReturn(Optional.of(archivedExercise))
    }
}