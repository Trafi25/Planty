package com.traffipart.polanty.domain.usecase.care

import com.traffipart.polanty.domain.reminder.CareReminderScheduler
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import javax.inject.Inject

/**
 * Use case for updating the due date timestamp of an existing care task and rescheduling its reminder notification.
 *
 * @property careTaskRepository The repository to persist the updated due date.
 * @property careReminderScheduler The scheduler to update the pending notification reminder.
 */
class UpdateCareTaskDueAtUseCase
    @Inject
    constructor(
        private val careTaskRepository: CareTaskRepository,
        private val careReminderScheduler: CareReminderScheduler,
    ) {
        /**
         * Updates the due date timestamp for the specified task.
         *
         * @param taskId Unique identifier of the target care task.
         * @param dueAt New due date timestamp in milliseconds.
         * @return `true` if the task was found and updated successfully, `false` otherwise.
         * @throws IllegalArgumentException If task ID or dueAt timestamp are invalid.
         */
        suspend operator fun invoke(
            taskId: Long,
            dueAt: Long,
        ): Boolean {
            require(taskId > 0) { "Task ID must be valid" }
            require(dueAt > 0) { "Due timestamp must be valid" }

            val updated = careTaskRepository.updateDueAt(taskId = taskId, dueAt = dueAt)
            if (!updated) return false
            careReminderScheduler.schedule(taskId = taskId, dueAt = dueAt)
            return true
        }
    }
