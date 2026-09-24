package com.traffipart.polanty.domain.usecase.care

import com.traffipart.polanty.domain.care.CareEngine
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import com.traffipart.polanty.domain.repository.knowledge.PlantKnowledgeRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for refreshing a plant's care schedule based on its botanical knowledge and task history.
 *
 * This use case fetches the species knowledge, retrieves existing care tasks, delegates calculation
 * to [CareEngine], and persists any newly calculated tasks.
 *
 * @property careEngine The engine that calculates scheduling intervals and prevents duplicate tasks.
 * @property careTaskRepository The repository to query existing tasks.
 * @property plantKnowledgeRepository The repository to fetch botanical care requirements.
 * @property saveCareTaskUseCase The use case to save new tasks.
 */
class RefreshPlantCarePlanUseCase
    @Inject
    constructor(
        private val careEngine: CareEngine,
        private val careTaskRepository: CareTaskRepository,
        private val plantKnowledgeRepository: PlantKnowledgeRepository,
        private val saveCareTaskUseCase: SaveCareTaskUseCase,
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

            val knowledge = plantKnowledgeRepository.getPlantKnowledge(scientificName) ?: return emptyList()
            val existingTasks = careTaskRepository.observePlantTasks(plantId).first()

            val newTasks = careEngine.generateTasks(plantId, knowledge, existingTasks, now)

            val createdTaskIds = mutableListOf<Long>()

            for (task in newTasks) {
                val id = saveCareTaskUseCase(task)
                createdTaskIds += id
            }
            return createdTaskIds
        }
    }
