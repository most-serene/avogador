package eu.mostserene.avogador.exerciseservice.controllers.mocks

import eu.mostserene.avogador.exerciseservice.antiplagiarism.AntiPlagiarismService
import eu.mostserene.avogador.exerciseservice.antiplagiarism.PlagiarismReport
import eu.mostserene.avogador.exerciseservice.antiplagiarism.similarityreport.SimilarityReport
import eu.mostserene.avogador.exerciseservice.controllers.AbstractControllerTests
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import java.util.*

class AntiPlagiarismServiceMocks(private val antiPlagiarismService: AntiPlagiarismService) : AbstractControllerTests() {

    fun setup() {
        `when`(antiPlagiarismService.getSimilarityReport(any()))
            .thenReturn(Optional.empty())
        `when`(antiPlagiarismService.getSimilarityReport(visibleExercise))
            .thenReturn(Optional.of(SimilarityReport()))
        `when`(antiPlagiarismService.retrieveSimilarityReportFile(any()))
            .thenReturn(Optional.empty())
        `when`(antiPlagiarismService.retrieveSimilarityReportFile(visibleExercise))
            .thenReturn(Optional.of(PlagiarismReport()))
    }
}