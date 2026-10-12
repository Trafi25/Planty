package com.traffipart.polanty.domain.sync

/** Schedules recurring background updates to environment-aware plant care plans. */
interface CarePlanSyncScheduler {
    /** Ensures a single recurring synchronization job is scheduled. */
    fun schedulePeriodicSync()
}
