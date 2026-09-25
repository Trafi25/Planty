package com.traffipart.polanty.domain.usecase.care

import com.traffipart.polanty.domain.model.CareTask
import com.traffipart.polanty.domain.model.CareTaskType
import javax.inject.Inject

class RecordSoilCheckResultUseCase
    @Inject
    constructor(
        private val completeCareTaskUseCase: CompleteCareTaskUseCase,
        private val saveCareTaskUseCase: SaveCareTaskUseCase,
        private val refreshPlantCarePlanUseCase: RefreshPlantCarePlanUseCase,
    ) {
        suspend operator fun invoke(
            taskId: Long,
            plantId: Long,
            scientificName: String,
            soilIsDry: Boolean,
            now: Long =
                System.currentTimeMillis(),
        ): Boolean {
            require(taskId > 0) { "Task ID must be valid" }
            require(plantId > 0) { "Plant ID must be valid" }
            val completed = completeCareTaskUseCase(taskId = taskId, completedAt = now)
            if (!completed) {
                return false
            }
            if (soilIsDry) {
                saveCareTaskUseCase(
                    CareTask(
                        id = 0L,
                        plantId = plantId,
                        type = CareTaskType.Water,
                        dueAt = now,
                        isCompleted = false,
                        completedAt = null,
                        xpReward = WATER_XP,
                    ),
                )
            } else {
                refreshPlantCarePlanUseCase(plantId = plantId, scientificName = scientificName, now = now)
            }
            return true
        }

        private companion object {
            const val WATER_XP = 10
        }
    }
