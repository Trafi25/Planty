package com.traffipart.polanty.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.traffipart.polanty.domain.sync.CarePlanSyncScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Schedules persistent weather-aware care synchronization with WorkManager.
 *
 * @property context Application context used to access WorkManager.
 */
class WorkManagerCarePlanSyncScheduler
    @Inject
    constructor(
        @ApplicationContext
        private val context: Context,
    ) : CarePlanSyncScheduler {
        /**
         * Enqueues unique work with a twelve-hour interval and a connected-network constraint.
         * Updates an existing request without restarting its periodic schedule. Actual execution
         * time depends on Android's background scheduling and network availability.
         */
        override fun schedulePeriodicSync() {
            val constraints =
                Constraints
                    .Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED,
                    ).build()

            val request =
                PeriodicWorkRequestBuilder<
                    CareEnvironmentSyncWorker,
                >(
                    SYNC_INTERVAL_HOURS,
                    TimeUnit.HOURS,
                ).setConstraints(
                    constraints,
                ).build()

            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    request,
                )
        }

        private companion object {
            const val WORK_NAME = "environment-care-sync"

            const val SYNC_INTERVAL_HOURS = 12L
        }
    }
