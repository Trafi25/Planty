package com.traffipart.polanty.data.remeinder

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.traffipart.polanty.domain.remeinder.CareReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkManagerCareReminderScheduler
    @Inject
    constructor(
        @ApplicationContext
        private val context: Context,
    ) : CareReminderScheduler {
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

        override fun cancel(taskId: Long) {
            WorkManager.getInstance(context).cancelUniqueWork(workName(taskId))
        }

        private fun workName(taskId: Long): String = "care-reminder-$taskId"
    }
