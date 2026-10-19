package com.traffipart.polanty.domain.usecase.location

import com.google.common.truth.Truth
import com.traffipart.polanty.domain.model.care.CareTask
import com.traffipart.polanty.domain.model.care.CareTaskType
import com.traffipart.polanty.domain.repository.care.CareEnvironmentRepository
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import com.traffipart.polanty.domain.repository.location.GardenLocationRepository
import com.traffipart.polanty.domain.usecase.care.SyncEnvironmentAwareCarePlansUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * Unit tests for [SyncEnvironmentAwareCarePlansUseCase], verifying short-circuit optimization conditions.
 */
class SyncEnvironmentAwareCarePlansUseCaseTest {
    private val tasks = mockk<CareTaskRepository>()
    private val location = mockk<GardenLocationRepository>()
    private val weather = mockk<CareEnvironmentRepository>()
    private val useCase =
        SyncEnvironmentAwareCarePlansUseCase(
            plantRepository = mockk(),
            plantSpaceRepository = mockk(),
            careTaskRepository = tasks,
            plantKnowledgeRepository = mockk(),
            gardenLocationRepository = location,
            careEnvironmentRepository = weather,
            careEnvironmentEstimator = mockk(),
            careEngine = mockk(),
            reschedulePolicy = mockk(),
            updateCareTaskDueAtUseCase = mockk(),
        )

    /**
     * Verifies that having no open care tasks short-circuits execution before querying location or weather.
     */
    @Test
    fun noOpenTasksSkipsLocationAndWeather() =
        runTest {
            every { tasks.observeOpenTasks() } returns flowOf(emptyList())

            Truth.assertThat(useCase()).isEqualTo(0)

            verify(exactly = 0) { location.observeLocation() }
            coVerify(exactly = 0) { weather.getCurrentEnvironment(any(), any()) }
        }

    /**
     * Verifies that open non-soil tasks (such as watering) short-circuit execution before querying location or weather.
     */
    @Test
    fun wateringOnlySkipsLocationAndWeather() =
        runTest {
            every { tasks.observeOpenTasks() } returns flowOf(listOf(task(CareTaskType.Water)))

            Truth.assertThat(useCase()).isEqualTo(0)

            verify(exactly = 0) { location.observeLocation() }
            coVerify(exactly = 0) { weather.getCurrentEnvironment(any(), any()) }
        }

    /**
     * Verifies that missing garden location coordinates short-circuit execution before calling the weather API.
     */
    @Test
    fun missingLocationSkipsWeather() =
        runTest {
            every { tasks.observeOpenTasks() } returns flowOf(listOf(task(CareTaskType.CheckSoil)))
            every { location.observeLocation() } returns flowOf(null)

            Truth.assertThat(useCase()).isEqualTo(0)

            coVerify(exactly = 0) { weather.getCurrentEnvironment(any(), any()) }
        }

    private fun task(type: CareTaskType) =
        CareTask(
            id = 1L,
            plantId = 1L,
            type = type,
            dueAt = 100_000L,
            isCompleted = false,
            completedAt = null,
            xpReward = 10,
        )
}
