package com.traffipart.polanty.domain.usecase.care

import com.traffipart.polanty.domain.model.CareTask
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import javax.inject.Inject

/**
 * Use case for creating and persisting a new plant care task.
 *
 * This use case ensures that the task is valid (associated with a plant, has a due date,
 * and a non-negative XP reward) before delegating to the repository.
 *
 * @property careRepository The repository to persist the task.
 */
class SaveCareTaskUseCase
    @Inject
    constructor(
        private val careRepository: CareTaskRepository,
    ) {
        /**
         * Saves a new care task.
         *
         * @param task The [CareTask] domain model to save.
         * @return The unique ID of the newly created task.
         * @throws IllegalArgumentException If any of the task validation rules fail.
         */
        suspend operator fun invoke(task: CareTask): Long {
            require(task.plantId > 0) {
                "CareTask must belong to a valid plant"
            }
            require(task.dueAt > 0) {
                "CareTask must have a valid due timestamp"
            }
            require(task.xpReward >= 0) {
                "XP reward cannot be negative"
            }
            return careRepository.createTask(task)
        }
    }
