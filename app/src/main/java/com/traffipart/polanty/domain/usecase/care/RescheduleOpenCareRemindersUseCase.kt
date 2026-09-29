package com.traffipart.polanty.domain.usecase.care

import com.traffipart.polanty.domain.reminder.CareReminderScheduler
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case to reschedule all active open care reminders.
 *
 * Typically invoked upon application launch or when notification permissions are granted
 * to ensure all pending care reminders are registered with [CareReminderScheduler].
 *
 * @property careTaskRepository The repository to fetch open care tasks.
 * @property reminderScheduler The scheduler to enqueue work for open tasks.
 */
class RescheduleOpenCareRemindersUseCase
    @Inject
    constructor(
        private val careTaskRepository: CareTaskRepository,
        private val reminderScheduler: CareReminderScheduler,
    ) {
        /**
         * Fetches all current open care tasks and schedules their notification reminders.
         */
        suspend operator fun invoke() {
            val openTasks = careTaskRepository.observeOpenTasks().first()
            openTasks.forEach { task ->
                reminderScheduler.schedule(task.id, dueAt = task.dueAt)
            }
        }
    }
