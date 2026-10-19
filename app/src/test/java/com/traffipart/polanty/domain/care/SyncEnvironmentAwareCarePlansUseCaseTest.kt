package com.traffipart.polanty.domain.care

import com.google.common.truth.Truth.assertThat
import com.traffipart.polanty.domain.repository.care.CareEnvironmentRepository
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import com.traffipart.polanty.domain.repository.knowledge.PlantKnowledgeRepository
import com.traffipart.polanty.domain.repository.location.GardenLocationRepository
import com.traffipart.polanty.domain.repository.plant.PlantRepository
import com.traffipart.polanty.domain.repository.plant.PlantSpaceRepository
import com.traffipart.polanty.domain.usecase.care.SyncEnvironmentAwareCarePlansUseCase
import com.traffipart.polanty.domain.usecase.care.UpdateCareTaskDueAtUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SyncEnvironmentAwareCarePlansUseCaseTest {
    private val plantRepository = mockk<PlantRepository>()

    private val plantSpaceRepository = mockk<PlantSpaceRepository>()

    private val careTaskRepository = mockk<CareTaskRepository>()

    private val plantKnowledgeRepository = mockk<PlantKnowledgeRepository>()

    private val gardenLocationRepository = mockk<GardenLocationRepository>()

    private val careEnvironmentRepository = mockk<CareEnvironmentRepository>()

    private val careEnvironmentEstimator = mockk<CareEnvironmentEstimator>()

    private val careEngine = mockk<CareEngine>()

    private val reschedulePolicy = mockk<CareTaskReschedulePolicy>()

    private val updateCareTaskDueAtUseCase = mockk<UpdateCareTaskDueAtUseCase>()

    private val useCase =
        SyncEnvironmentAwareCarePlansUseCase(
            plantRepository = plantRepository,
            plantSpaceRepository = plantSpaceRepository,
            careTaskRepository = careTaskRepository,
            plantKnowledgeRepository = plantKnowledgeRepository,
            gardenLocationRepository = gardenLocationRepository,
            careEnvironmentRepository = careEnvironmentRepository,
            careEnvironmentEstimator = careEnvironmentEstimator,
            careEngine = careEngine,
            reschedulePolicy = reschedulePolicy,
            updateCareTaskDueAtUseCase =
            updateCareTaskDueAtUseCase,
        )

    @Test
    fun noOpenTasksSkipsLocationAndWeather() =
        runTest {
            every {
                careTaskRepository.observeOpenTasks()
            } returns flowOf(emptyList())

            val result =
                useCase(
                    now = 1_700_000_000_000L,
                )

            assertThat(result).isEqualTo(0)

            verify(exactly = 0) {
                gardenLocationRepository.observeLocation()
            }

            coVerify(exactly = 0) {
                careEnvironmentRepository
                    .getCurrentEnvironment(
                        any(),
                        any(),
                    )
            }
        }
}
