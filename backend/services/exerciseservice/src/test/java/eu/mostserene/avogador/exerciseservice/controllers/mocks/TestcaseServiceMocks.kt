package eu.mostserene.avogador.exerciseservice.controllers.mocks

import eu.mostserene.avogador.exerciseservice.controllers.AbstractControllerTests
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseService
import org.mockito.Mockito.*
import java.util.*

class TestcaseServiceMocks(private val testcaseService: TestcaseService) : AbstractControllerTests() {

    fun setup() {
        `when`(testcaseService.getTestcasesFromExercise(eq(visibleCodingExercise)))
            .thenReturn(listOf(visibleTestcaseDto, hiddenTestcaseDto))
        `when`(testcaseService.getTestcase(any(), any()))
            .thenReturn(Optional.empty())
        `when`(testcaseService.getTestcase(eq(visibleCodingExercise), eq(visibleTestcaseDto.id)))
            .thenReturn(Optional.of(visibleTestcaseDto))
        `when`(testcaseService.getTestcase(eq(visibleCodingExercise), eq(hiddenTestcaseDto.id)))
            .thenReturn(Optional.of(hiddenTestcaseDto))
        `when`(testcaseService.getSimpleTestcasesFromExercise(any()))
            .thenReturn(listOf())
        `when`(testcaseService.getSimpleTestcasesFromExercise(eq(visibleCodingExercise)))
            .thenReturn(listOf(simpleVisibleTestcase, simpleHiddenTestcase))
        `when`(testcaseService.getSimpleTestcase(any()))
            .thenReturn(Optional.empty())
        `when`(testcaseService.getSimpleTestcase(eq(visibleTestcaseDto.id)))
            .thenReturn(Optional.of(visibleTestcase))
    }
}