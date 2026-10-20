package com.traffipart.polanty.domain.usecase.care

import com.traffipart.polanty.domain.care.CareEngine
import com.traffipart.polanty.domain.care.CareEnvironmentEstimator
import com.traffipart.polanty.domain.care.DaylightDurationCalculator
import com.traffipart.polanty.domain.model.care.CareContext
import com.traffipart.polanty.domain.repository.care.CareEnvironmentRepository
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import com.traffipart.polanty.domain.repository.knowledge.PlantKnowledgeRepository
import com.traffipart.polanty.domain.repository.location.GardenLocationRepository
import com.traffipart.polanty.domain.repository.plant.PlantRepository
import com.traffipart.polanty.domain.repository.plant.PlantSpaceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for refreshing a plant's care schedule based on its botanical knowledge, task history, and environmental conditions.
 *
 * This use case fetches the species knowledge, queries the local room exposure and weather environment,
 * retrieves existing care tasks, delegates calculation to [CareEngine], and persists any newly calculated tasks.
 *
 * @property careEngine The engine that calculates scheduling intervals and prevents duplicate tasks.
 * @property careTaskRepository The repository to query existing tasks.
 * @property plantKnowledgeRepository The repository to fetch botanical care requirements.
 * @property saveCareTaskUseCase The use case to save new tasks.
 * @property plantRepository The repository to fetch target plant information.
 * @property plantSpaceRepository The repository to query room/space climate exposure.
 * @property gardenLocationRepository The repository to retrieve saved garden location coordinates.
 * @property careEnvironmentRepository The repository to fetch outdoor weather environment conditions.
 * @property careEnvironmentEstimator Estimator used to adjust outdoor weather for room climate exposure.
 * @property daylightDurationCalculator Calculator used to determine astronomical photoperiod/daylight duration.
 */
class RefreshPlantCarePlanUseCase
    @Inject
    constructor(
        private val careEngine: CareEngine,
        private val careTaskRepository: CareTaskRepository,
        private val plantKnowledgeRepository: PlantKnowledgeRepository,
        private val saveCareTaskUseCase: SaveCareTaskUseCase,
        private val plantRepository: PlantRepository,
        private val plantSpaceRepository: PlantSpaceRepository,
        private val gardenLocationRepository: GardenLocationRepository,
        private val careEnvironmentRepository: CareEnvironmentRepository,
        private val careEnvironmentEstimator: CareEnvironmentEstimator,
        private val daylightDurationCalculator: DaylightDurationCalculator,
    ) {
        /**
         * Evaluates care requirements and creates newly needed care tasks for a specific plant.
         *
         * @param plantId The unique ID of the plant.
         * @param scientificName The scientific botanical name used to look up care requirements.
         * @param now The current reference timestamp in milliseconds.
         * @return A list of newly created care task IDs.
         */
        suspend operator fun invoke(
            plantId: Long,
            scientificName: String,
            now: Long = System.currentTimeMillis(),
        ): List<Long> {
            require(plantId > 0) {
                "Plant ID must be valid"
            }
            require(scientificName.isNotBlank()) { "Scientific name must not be blank" }
            val careContext =
                getCareContextForPlant(
                    plantId = plantId,
                    now = now,
                )
            val knowledge = plantKnowledgeRepository.getPlantKnowledge(scientificName) ?: return emptyList()
            val existingTasks = careTaskRepository.observePlantTasks(plantId).first()

            val newTasks = careEngine.generateTasks(plantId, knowledge, existingTasks, careContext, now)

            val createdTaskIds = mutableListOf<Long>()

            for (task in newTasks) {
                val id = saveCareTaskUseCase(task)
                createdTaskIds += id
            }
            return createdTaskIds
        }

        /**
         * Resolves the full [CareContext] (microclimate and daylight hours) for a target plant.
         *
         * @param plantId Target plant ID.
         * @param now Current reference timestamp in milliseconds.
         * @return Populated [CareContext].
         */
        private suspend fun getCareContextForPlant(
            plantId: Long,
            now: Long,
        ): CareContext {
            val coordinates =
                gardenLocationRepository
                    .observeLocation()
                    .first()
                    ?: return CareContext()

            val daylightHours =
                daylightDurationCalculator
                    .calculateHours(
                        latitude = coordinates.latitude,
                        atMillis = now,
                    )

            val plant =
                plantRepository
                    .observePlant(plantId)
                    .first()
                    ?: return CareContext(
                        daylightHours =
                            daylightHours,
                    )

            val space =
                plant.spaceId
                    ?.let { spaceId ->
                        plantSpaceRepository
                            .observeSpace(spaceId)
                            .first()
                    }

            val outdoorEnvironment =
                if (space != null) {
                    careEnvironmentRepository
                        .getCurrentEnvironment(
                            latitude =
                                coordinates.latitude,
                            longitude =
                                coordinates.longitude,
                        )
                } else {
                    null
                }

            val environment =
                if (outdoorEnvironment != null && space != null) {
                    careEnvironmentEstimator
                        .estimate(
                            outdoorEnvironment = outdoorEnvironment,
                            exposure = space.type.climateExposure,
                        )
                } else {
                    null
                }

            return CareContext(environment = environment, daylightHours = daylightHours)
        }
    }
