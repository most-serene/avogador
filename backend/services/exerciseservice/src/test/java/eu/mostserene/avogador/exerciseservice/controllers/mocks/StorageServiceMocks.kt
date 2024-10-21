package eu.mostserene.avogador.exerciseservice.controllers.mocks

import eu.mostserene.avogador.exerciseservice.controllers.AbstractControllerTests
import eu.mostserene.avogador.exerciseservice.storage.StorageService
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import java.util.*

class StorageServiceMocks(private val storageService: StorageService) : AbstractControllerTests() {

    fun setup() {
        `when`(storageService.getExerciseTemplate(any()))
            .thenReturn(Optional.empty())
        `when`(storageService.getExerciseTemplate(visibleExercise))
            .thenReturn(Optional.of(template))
    }
}