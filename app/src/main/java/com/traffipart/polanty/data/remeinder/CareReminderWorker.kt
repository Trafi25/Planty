package com.traffipart.polanty.data.remeinder

import android.app.NotificationManager
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import com.traffipart.polanty.domain.repository.plant.PlantRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class CareReminderWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val careTaskRepository: CareTaskRepository,
        private val plantRepository: PlantRepository,
        private val notificationManager: CareNotificationManager,
    ) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val taskId = inputData.getLong(KEY_TASK_ID, INVALID_TASK_ID)

            if (taskId <= 0) {
                return Result.failure()
            }

            val task = careTaskRepository.getTask(taskId) ?: return Result.success()
            if (task.isCompleted) return Result.success()
            val plant = plantRepository.observePlant(task.plantId).first() ?: return Result.success()
            notificationManager.showCareReminder(
                task = task,
                plantName = plant.displayName,
            )

            return Result.success()
        }

        companion object {
            const val KEY_TASK_ID = "care_task_id"

            private const val INVALID_TASK_ID = -1L
        }
    }
