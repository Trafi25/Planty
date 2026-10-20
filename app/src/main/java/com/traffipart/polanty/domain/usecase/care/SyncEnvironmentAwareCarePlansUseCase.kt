package com.traffipart.polanty.domain.usecase.care

import com.traffipart.polanty.domain.care.CareEngine
import com.traffipart.polanty.domain.care.CareEnvironmentEstimator
import com.traffipart.polanty.domain.care.CareTaskReschedulePolicy
import com.traffipart.polanty.domain.care.DaylightDurationCalculator
import com.traffipart.polanty.domain.model.care.CareContext
import com.traffipart.polanty.domain.model.care.CareTaskType
import com.traffipart.polanty.domain.repository.care.CareEnvironmentRepository
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import com.traffipart.polanty.domain.repository.knowledge.PlantKnowledgeRepository
import com.traffipart.polanty.domain.repository.location.GardenLocationRepository
import com.traffipart.polanty.domain.repository.plant.PlantRepository
import com.traffipart.polanty.domain.repository.plant.PlantSpaceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case to synchronize and update the due dates of all open care tasks based on live weather data,
 * room climate exposure estimates, and astronomical daylight duration.
 *
 * This use case evaluates current outdoor weather, estimates room microclimates, calculates photoperiod daylight length,
 * delegates recommended due date calculations to [CareEngine], and reschedules open soil check tasks if the recommended
 * due date differs significantly according to [CareTaskReschedulePolicy].
 *
 * @property plantRepository Repository to query plant records.
 * @property plantSpaceRepository Repository to query room climate exposure levels.
 * @property careTaskRepository Repository to query open care tasks.
 * @property plantKnowledgeRepository Repository to query species botanical guidelines.
 * @property gardenLocationRepository Repository to query saved garden location coordinates.
 * @property careEnvironmentRepository Repository to fetch outdoor weather conditions.
 * @property careEnvironmentEstimator Estimator to adjust outdoor weather for room climate exposure.
 * @property careEngine Engine calculating recommended due dates for tasks.
 * @property reschedulePolicy Policy deciding whether a due date shift warrants rescheduling.
 * @property updateCareTaskDueAtUseCase Use case to update due dates and reschedule notifications.
 * @property daylightDurationCalculator Calculator used to compute daylight duration in hours for the garden location.
 */
class SyncEnvironmentAwareCarePlansUseCase
    @Inject
    constructor(
        private val plantRepository: PlantRepository,
        private val plantSpaceRepository: PlantSpaceRepository,
        private val careTaskRepository: CareTaskRepository,
        private val plantKnowledgeRepository: PlantKnowledgeRepository,
        private val gardenLocationRepository: GardenLocationRepository,
        private val careEnvironmentRepository: CareEnvironmentRepository,
        private val careEnvironmentEstimator: CareEnvironmentEstimator,
        private val careEngine: CareEngine,
        private val reschedulePolicy: CareTaskReschedulePolicy,
        private val updateCareTaskDueAtUseCase: UpdateCareTaskDueAtUseCase,
        private val daylightDurationCalculator: DaylightDurationCalculator,
    ) {
        /**
         * Fetches weather data for saved garden coordinates and updates open soil check task due dates.
         * Skips location and weather retrieval when no open soil checks exist. Missing coordinates
         * or unavailable weather also return zero without updating tasks.
         *
         * @param now Current reference timestamp in milliseconds.
         * @return The number of open care tasks successfully rescheduled.
         */
        suspend operator fun invoke(now: Long = System.currentTimeMillis()): Int {
            val openSoilTasks =
                careTaskRepository
                    .observeOpenTasks()
                    .first()
                    .filter { task ->
                        task.type == CareTaskType.CheckSoil
                    }

            if (openSoilTasks.isEmpty()) {
                return 0
            }

            val coordinates =
                gardenLocationRepository
                    .observeLocation()
                    .first()
                    ?: return 0

            val daylightHours =
                daylightDurationCalculator
                    .calculateHours(latitude = coordinates.latitude, atMillis = now)

            val outdoorEnvironment =
                careEnvironmentRepository
                    .getCurrentEnvironment(
                        latitude = coordinates.latitude,
                        longitude = coordinates.longitude,
                    )

            val plantsById =
                plantRepository
                    .observePlants()
                    .first()
                    .associateBy { plant ->
                        plant.id
                    }

            val spacesById =
                plantSpaceRepository
                    .observeSpaces()
                    .first()
                    .associateBy { space ->
                        space.id
                    }

            var updatedCount = 0

            for (task in openSoilTasks) {
                val plant = plantsById[task.plantId] ?: continue

                val spaceId = plant.spaceId ?: continue

                val space = plant.spaceId.let { spaceId -> spacesById[spaceId] }

                val knowledge =
                    plantKnowledgeRepository
                        .getPlantKnowledge(
                            plant.scientificName,
                        )
                        ?: continue

                val plantTasks =
                    careTaskRepository
                        .observePlantTasks(
                            plant.id,
                        ).first()

                val environment =
                    if (
                        space != null &&
                        outdoorEnvironment != null
                    ) {
                        careEnvironmentEstimator
                            .estimate(
                                outdoorEnvironment = outdoorEnvironment,
                                exposure = space.type.climateExposure,
                            )
                    } else {
                        null
                    }

                val careContext =
                    CareContext(
                        environment = environment,
                        daylightHours =
                            daylightHours,
                    )

                val recommendedDueAt =
                    careEngine
                        .calculateRecommendedSoilCheckDueAt(
                            plantId = plant.id,
                            knowledge = knowledge,
                            existingTasks = plantTasks,
                            careContext = careContext,
                            now = now,
                        )
                        ?: continue

                val shouldReschedule =
                    reschedulePolicy
                        .shouldReschedule(
                            currentDueAt = task.dueAt,
                            recommendedDueAt = recommendedDueAt,
                            now = now,
                        )

                if (!shouldReschedule) continue

                val updated =
                    updateCareTaskDueAtUseCase(
                        taskId = task.id,
                        dueAt = maxOf(now, recommendedDueAt),
                    )

                if (updated) updatedCount++
            }

            return updatedCount
        }
    }
