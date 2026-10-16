package com.traffipart.polanty.data.reminder

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * [CoroutineWorker] executed by Android [androidx.work.WorkManager] to post care task reminder notifications.
 *
 * Verifies the triggering task is still due, then combines all due tasks and their plant names
 * into one notification via [CareNotificationManager]. Future tasks are excluded.
 *
 * @param context The application context.
 * @param params Worker configuration parameters passed by WorkManager.
 * @property careTaskRepository Repository used to retrieve the target care task.
 * @property notificationManager Manager responsible for posting the system notification.
 */
@HiltWorker
class CareReminderWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val careTaskRepository: CareTaskRepository,
        private val notificationManager: CareNotificationManager,
    ) : CoroutineWorker(context, params) {
        /**
         * Execution entry point for the background worker.
         *
         * @return [Result.success] if processing succeeded or was skipped (e.g., task completed),
         * or [Result.failure] if mandatory parameters were invalid.
         */
        override suspend fun doWork(): Result {
            val taskId = inputData.getLong(KEY_TASK_ID, INVALID_TASK_ID)

            if (taskId <= 0) {
                return Result.failure()
            }

            val task = careTaskRepository.getTask(taskId) ?: return Result.success()
            val now = System.currentTimeMillis()
            if (task.isCompleted || task.dueAt > now) return Result.success()
            notificationManager.refreshReminder()

            return Result.success()
        }

        companion object {
            /** Key for passing the target care task ID in WorkManager input data. */
            const val KEY_TASK_ID = "care_task_id"

            private const val INVALID_TASK_ID = -1L
        }
    }
