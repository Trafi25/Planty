package com.traffipart.polanty.domain.usecase.care

import com.traffipart.polanty.domain.reminder.CareReminderRefresher
import com.traffipart.polanty.domain.reminder.CareReminderScheduler
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import javax.inject.Inject

/**
 * Completes a plant care task, cancels its scheduled work, and refreshes the visible care summary.
 *
 * @property careRepository The repository to update the task status.
 * @property careReminderScheduler The scheduler to cancel the pending reminder notification.
 * @property careReminderRefresher Updates or removes an already-visible care notification.
 */
class CompleteCareTaskUseCase
    @Inject
    constructor(
        private val careRepository: CareTaskRepository,
        private val careReminderScheduler: CareReminderScheduler,
        private val careReminderRefresher: CareReminderRefresher,
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
            val completed = careRepository.completeTask(taskId, completedAt)
            if (completed) {
                careReminderScheduler.cancel(taskId)
                careReminderRefresher.refreshVisibleReminder()
            }
            return completed
        }
    }
