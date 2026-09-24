package com.traffipart.polanty.domain.usecase.care

import com.traffipart.polanty.domain.care.CareEngine
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import com.traffipart.polanty.domain.repository.knowledge.PlantKnowledgeRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RefreshPlantCarePlanUseCase
    @Inject
    constructor(
        private val careEngine: CareEngine,
        private val careTaskRepository: CareTaskRepository,
        private val plantKnowledgeRepository: PlantKnowledgeRepository,
        private val saveCareTaskUseCase: SaveCareTaskUseCase,
    ) {
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
