package eu.mostserene.avogador.exerciseservice.controllers.mocks

import eu.mostserene.avogador.exerciseservice.controllers.AbstractControllerTests
import eu.mostserene.avogador.exerciseservice.exercises.multiplechoiceexercises.MultipleChoiceService
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import java.util.*

class MultipleChoiceServiceMocks(private val multipleChoiceService: MultipleChoiceService) : AbstractControllerTests() {

    fun setup() {
        `when`(multipleChoiceService.getMultipleChoiceExercise(any()))
            .thenReturn(Optional.empty())
        `when`(multipleChoiceService.getMultipleChoiceExercise(visibleMultipleChoiceExercise.id))
            .thenReturn(Optional.of(visibleMultipleChoiceExercise))
        `when`(multipleChoiceService.getMultipleChoiceExercise(hiddenMultipleChoiceExercise.id))
            .thenReturn(Optional.of(hiddenMultipleChoiceExercise))
        `when`(multipleChoiceService.getMultipleChoiceExercise(archivedMultipleChoiceExercise.id))
            .thenReturn(Optional.of(archivedMultipleChoiceExercise))

        `when`(multipleChoiceService.getExerciseOptions(any()))
            .thenReturn(listOf())
        `when`(multipleChoiceService.getExerciseOptions(visibleMultipleChoiceExercise.id))
            .thenReturn(listOf(option1, option2, option3, option4))
        `when`(multipleChoiceService.getExerciseOptions(hiddenMultipleChoiceExercise.id))
            .thenReturn(listOf(option1, option2, option3))

        `when`(multipleChoiceService.updateMultipleChoiceExercise(visibleMultipleChoiceExercise))
            .thenReturn(visibleMultipleChoiceExercise)
    }
}