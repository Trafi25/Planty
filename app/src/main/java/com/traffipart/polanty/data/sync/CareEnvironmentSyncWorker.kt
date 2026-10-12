package com.traffipart.polanty.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.traffipart.polanty.domain.usecase.care.SyncEnvironmentAwareCarePlansUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.coroutines.cancellation.CancellationException

/**
 * Synchronizes open soil check schedules with current weather and room climate estimates.
 *
 * @property syncEnvironmentAwareCarePlansUseCase Updates due dates and their notification reminders.
 */
@HiltWorker
class CareEnvironmentSyncWorker
    @AssistedInject
    constructor(
        @Assisted
        context: Context,
        @Assisted
        workerParameters: WorkerParameters,
        private val syncEnvironmentAwareCarePlansUseCase: SyncEnvironmentAwareCarePlansUseCase,
    ) : CoroutineWorker(
            context,
            workerParameters,
        ) {
        /**
         * Runs one synchronization pass. A pass with no eligible tasks or missing location/weather
         * succeeds without changes. Unexpected failures request a retry; cancellation propagates.
         */
        override suspend fun doWork(): Result =
            try {
                syncEnvironmentAwareCarePlansUseCase()
                Result.success()
            } catch (
                e: CancellationException,
            ) {
                throw e
            } catch (
                _: Exception,
            ) {
                Result.retry()
            }
    }
