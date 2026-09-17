package com.traffipart.polanty.domain.usecase.care

import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import javax.inject.Inject

/**
 * Use case for marking a plant care task as completed.
 *
 * @property careRepository The repository to update the task status.
 */
class CompleteCareTaskUseCase
    @Inject
    constructor(
        private val careRepository: CareTaskRepository,
    ) {
        /**
         * Completes a specific care task.
         *
         * @param taskId The unique ID of the task to mark as completed.
         * @param completedAt The timestamp of completion, defaults to current time.
         * @return True if the task was successfully completed, false otherwise.
         */
        suspend operator fun invoke(
            taskId: Long,
            completedAt: Long = System.currentTimeMillis(),
        ): Boolean {
            require(taskId > 0) {
                "Task ID must be valid"
            }
            return careRepository.completeTask(taskId, completedAt)
        }
    }
