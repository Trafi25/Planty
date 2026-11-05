package com.traffipart.polanty.domain.usecase.health

import com.google.common.truth.Truth.assertThat
import com.traffipart.polanty.core.common.Result
import com.traffipart.polanty.domain.error.PlantHealthError
import com.traffipart.polanty.domain.model.health.PlantHealthAssessment
import com.traffipart.polanty.domain.model.health.PlantHealthFinding
import com.traffipart.polanty.domain.model.health.PlantHealthIssueCategory
import com.traffipart.polanty.domain.model.health.PlantHealthStatus
import com.traffipart.polanty.domain.model.plant.PlantImage
import com.traffipart.polanty.domain.repository.health.PlantHealthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AssessPlantHealthUseCaseTest {
    private val repository = mockk<PlantHealthRepository>()
    private val useCase = AssessPlantHealthUseCase(repository)
    private val image = PlantImage(byteArrayOf(1, 2, 3), "leaf.jpg", "image/jpeg")
    private val assessment =
        PlantHealthAssessment(
            status = PlantHealthStatus.NeedsAttention,
            summary = "Visible leaf damage",
            findings = listOf(PlantHealthFinding(PlantHealthIssueCategory.Unknown, "Leaf damage", "Cause is uncertain")),
            careRecommendations = listOf("Inspect the leaves more closely"),
        )

    @Test
    fun rejectsEmptyImageWithoutCallingRepository() =
        runTest {
            assertThat(useCase(image.copy(bytes = byteArrayOf()))).isEqualTo(Result.Error(PlantHealthError.InvalidImage))
            coVerify(exactly = 0) { repository.assessPlantHealth(any(), any()) }
        }

    @Test
    fun rejectsUnsupportedMimeTypesWithoutCallingRepository() =
        runTest {
            listOf("image/gif", "image/webp", "text/plain", "").forEach {
                assertThat(useCase(image.copy(mimeType = it))).isEqualTo(Result.Error(PlantHealthError.InvalidImage))
            }
            coVerify(exactly = 0) { repository.assessPlantHealth(any(), any()) }
        }

    @Test
    fun acceptsJpegAndPngAndPreservesAssessment() =
        runTest {
            val expected = Result.Success(assessment)
            coEvery { repository.assessPlantHealth(any(), null) } returns expected
            listOf("image/jpeg", "image/png").forEach {
                assertThat(useCase(image.copy(mimeType = it))).isSameInstanceAs(expected)
            }
            coVerify(exactly = 2) { repository.assessPlantHealth(any(), null) }
        }

    @Test
    fun trimsKnownSpeciesName() =
        runTest {
            coEvery { repository.assessPlantHealth(image, "Monstera deliciosa") } returns Result.Success(assessment)
            useCase(image, "  Monstera deliciosa  ")
            coVerify(exactly = 1) { repository.assessPlantHealth(image, "Monstera deliciosa") }
        }

    @Test
    fun missingOrBlankSpeciesIsOptional() =
        runTest {
            coEvery { repository.assessPlantHealth(image, null) } returns Result.Success(assessment)
            useCase(image)
            useCase(image, "  ")
            coVerify(exactly = 2) { repository.assessPlantHealth(image, null) }
        }

    @Test
    fun preservesHealthSpecificErrors() =
        runTest {
            listOf(
                PlantHealthError.NoPlantDetected,
                PlantHealthError.InvalidImage,
                PlantHealthError.NoInternet,
                PlantHealthError.Unauthorized,
                PlantHealthError.RequestTimeout,
                PlantHealthError.TooManyRequests,
                PlantHealthError.ServerError,
                PlantHealthError.InvalidResponse,
                PlantHealthError.Unknown,
            ).forEach { error ->
                val expected = Result.Error(error)
                coEvery { repository.assessPlantHealth(image, null) } returns expected
                assertThat(useCase(image)).isSameInstanceAs(expected)
            }
        }

    @Test
    fun inconclusiveAssessmentRemainsUncertain() =
        runTest {
            val uncertain = assessment.copy(status = PlantHealthStatus.Uncertain, findings = emptyList())
            coEvery { repository.assessPlantHealth(image, null) } returns Result.Success(uncertain)
            assertThat(useCase(image)).isEqualTo(Result.Success(uncertain))
        }

    @Test
    fun propagatesCoroutineCancellation() =
        runTest {
            coEvery { repository.assessPlantHealth(image, null) } throws CancellationException("Cancelled")
            assertThrows<CancellationException> { useCase(image) }
        }
}
