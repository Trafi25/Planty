package com.traffipart.polanty.domain.usecase.location

import com.google.common.truth.Truth
import com.traffipart.polanty.domain.care.CareEngine
import com.traffipart.polanty.domain.care.CareEnvironmentEstimator
import com.traffipart.polanty.domain.care.CareTaskReschedulePolicy
import com.traffipart.polanty.domain.care.DaylightDurationCalculator
import com.traffipart.polanty.domain.location.GeoCoordinates
import com.traffipart.polanty.domain.model.care.CareEnvironment
import com.traffipart.polanty.domain.model.care.CareEnvironmentSource
import com.traffipart.polanty.domain.model.care.CareTask
import com.traffipart.polanty.domain.model.care.CareTaskType
import com.traffipart.polanty.domain.model.knowledge.PlantKnowledge
import com.traffipart.polanty.domain.model.plant.Plant
import com.traffipart.polanty.domain.model.space.PlantSpace
import com.traffipart.polanty.domain.model.space.PlantSpaceType
import com.traffipart.polanty.domain.repository.care.CareEnvironmentRepository
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import com.traffipart.polanty.domain.repository.knowledge.PlantKnowledgeRepository
import com.traffipart.polanty.domain.repository.location.GardenLocationRepository
import com.traffipart.polanty.domain.repository.plant.PlantRepository
import com.traffipart.polanty.domain.repository.plant.PlantSpaceRepository
import com.traffipart.polanty.domain.usecase.care.SyncEnvironmentAwareCarePlansUseCase
import com.traffipart.polanty.domain.usecase.care.UpdateCareTaskDueAtUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.Instant

/**
 * Unit tests for [SyncEnvironmentAwareCarePlansUseCase], verifying short-circuit optimization conditions.
 */
class SyncEnvironmentAwareCarePlansUseCaseTest {
    private val tasks = mockk<CareTaskRepository>()
    private val location = mockk<GardenLocationRepository>()
    private val weather = mockk<CareEnvironmentRepository>()
    private val plants = mockk<PlantRepository>()
    private val spaces = mockk<PlantSpaceRepository>()
    private val knowledgeRepository = mockk<PlantKnowledgeRepository>()
    private val engine = mockk<CareEngine>()
    private val estimator = mockk<CareEnvironmentEstimator>()
    private val updateDueAt = mockk<UpdateCareTaskDueAtUseCase>()
    private val daylightCalculator = DaylightDurationCalculator()
    private val now = Instant.parse("2026-12-21T12:00:00Z").toEpochMilli()
    private val day = 86_400_000L
    private val useCase =
        SyncEnvironmentAwareCarePlansUseCase(
            plantRepository = plants,
            plantSpaceRepository = spaces,
            careTaskRepository = tasks,
            plantKnowledgeRepository = knowledgeRepository,
            gardenLocationRepository = location,
            careEnvironmentRepository = weather,
            careEnvironmentEstimator = estimator,
            careEngine = engine,
            reschedulePolicy = CareTaskReschedulePolicy(),
            updateCareTaskDueAtUseCase = updateDueAt,
            daylightDurationCalculator = daylightCalculator,
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

    @Test
    fun unassignedPlantIsRescheduledUsingDaylightWithoutWeather() =
        runTest {
            preparePlant()

            Truth.assertThat(useCase(now)).isEqualTo(1)

            verify {
                engine.calculateRecommendedSoilCheckDueAt(
                    plantId = 1L,
                    knowledge = any(),
                    existingTasks = any(),
                    careContext = match { it.environment == null && it.daylightHours == daylightCalculator.calculateHours(52.52, now) },
                    now = now,
                )
            }
            coVerify { updateDueAt(1L, now + 3 * day) }
            verify(exactly = 0) { estimator.estimate(any(), any()) }
        }

    @Test
    fun missingSpaceStillAllowsDaylightAdjustment() =
        runTest {
            preparePlant(spaceId = 99L)
            Truth.assertThat(useCase(now)).isEqualTo(1)
        }

    @Test
    fun missingWeatherStillAllowsDaylightForAssignedPlant() =
        runTest {
            preparePlant(spaceId = 10L, includeSpace = true)
            Truth.assertThat(useCase(now)).isEqualTo(1)
            verify(exactly = 0) { estimator.estimate(any(), any()) }
        }

    @Test
    fun combinesEstimatedRoomWeatherWithDaylight() =
        runTest {
            val outdoor = CareEnvironment(10.0, 70, CareEnvironmentSource.OutdoorWeather)
            val indoor = CareEnvironment(20.0, 50, CareEnvironmentSource.EstimatedIndoor)
            preparePlant(spaceId = 10L, includeSpace = true, environment = outdoor)
            every { estimator.estimate(outdoor, PlantSpaceType.LivingRoom.climateExposure) } returns indoor

            Truth.assertThat(useCase(now)).isEqualTo(1)

            verify {
                engine.calculateRecommendedSoilCheckDueAt(
                    1L,
                    any(),
                    any(),
                    match { it.environment == indoor && it.daylightHours != null },
                    now,
                )
            }
        }

    @Test
    fun overdueTasksAreNotMoved() =
        runTest {
            preparePlant()
            every { tasks.observeOpenTasks() } returns flowOf(listOf(task(CareTaskType.CheckSoil).copy(dueAt = now - 1)))
            Truth.assertThat(useCase(now)).isEqualTo(0)
            coVerify(exactly = 0) { updateDueAt(any(), any()) }
        }

    @Test
    fun unchangedDueDateDoesNotReschedule() =
        runTest {
            preparePlant()
            every { engine.calculateRecommendedSoilCheckDueAt(any(), any(), any(), any(), any()) } returns now + day
            Truth.assertThat(useCase(now)).isEqualTo(0)
            coVerify(exactly = 0) { updateDueAt(any(), any()) }
        }

    @Test
    fun failedUpdateIsNotCounted() =
        runTest {
            preparePlant()
            coEvery { updateDueAt(any(), any()) } returns false
            Truth.assertThat(useCase(now)).isEqualTo(0)
        }

    private fun preparePlant(
        spaceId: Long? = null,
        includeSpace: Boolean = false,
        environment: CareEnvironment? = null,
    ) {
        val openTask = task(CareTaskType.CheckSoil).copy(dueAt = now + day)
        every { tasks.observeOpenTasks() } returns flowOf(listOf(openTask))
        every { tasks.observePlantTasks(1L) } returns flowOf(listOf(openTask))
        every { location.observeLocation() } returns flowOf(GeoCoordinates(52.52, 13.4))
        coEvery { weather.getCurrentEnvironment(52.52, 13.4) } returns environment
        every { plants.observePlants() } returns flowOf(listOf(Plant(1L, "Monstera", null, null, spaceId, null)))
        every { spaces.observeSpaces() } returns
            flowOf(
                if (includeSpace) listOf(PlantSpace(10L, "Living room", PlantSpaceType.LivingRoom)) else emptyList(),
            )
        coEvery { knowledgeRepository.getPlantKnowledge("Monstera") } returns mockk<PlantKnowledge>()
        every { engine.calculateRecommendedSoilCheckDueAt(any(), any(), any(), any(), any()) } returns now + 3 * day
        coEvery { updateDueAt(any(), any()) } returns true
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
