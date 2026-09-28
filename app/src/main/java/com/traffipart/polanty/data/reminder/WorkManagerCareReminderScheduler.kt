package com.traffipart.polanty.data.reminder

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.traffipart.polanty.domain.reminder.CareReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * [CareReminderScheduler] implementation backed by Android [WorkManager].
 *
 * Schedules delayed [CareReminderWorker] tasks using unique work identifiers to ensure
 * exact or replacement behavior when reminders are updated or canceled.
 *
 * @property context The application context required to access [WorkManager].
 */
class WorkManagerCareReminderScheduler
    @Inject
    constructor(
        @ApplicationContext
        private val context: Context,
    ) : CareReminderScheduler {
        /**
         * Enqueues a one-time work request for [CareReminderWorker] after calculating initial delay.
         *
         * @param taskId The unique ID of the care task.
         * @param dueAt The epoch timestamp in milliseconds when the reminder is due.
         */
        override fun schedule(
            taskId: Long,
            dueAt: Long,
        ) {
            val now = System.currentTimeMillis()
            val delay = (dueAt - now).coerceAtLeast(0L)
            val request =
                OneTimeWorkRequestBuilder<CareReminderWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(workDataOf(CareReminderWorker.KEY_TASK_ID to taskId))
                    .build()
            WorkManager.getInstance(context).enqueueUniqueWork(workName(taskId), ExistingWorkPolicy.REPLACE, request)
        }

        /**
         * Cancels any pending work request matching the unique name for the given task ID.
         *
         * @param taskId The unique ID of the care task to cancel.
         */
        override fun cancel(taskId: Long) {
            WorkManager.getInstance(context).cancelUniqueWork(workName(taskId))
        }

        /**
         * Generates a unique work name for a specific care task ID.
         *
         * @param taskId The task identifier.
         * @return The formatted work identifier string.
         */
        private fun workName(taskId: Long): String = "care-reminder-$taskId"
    }
