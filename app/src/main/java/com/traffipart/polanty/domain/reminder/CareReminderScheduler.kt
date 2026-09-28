package com.traffipart.polanty.domain.reminder

/**
 * Interface defining operations for scheduling and canceling delayed notifications for care tasks.
 */
interface CareReminderScheduler {
    /**
     * Schedules a care reminder notification for a specific task at the designated timestamp.
     *
     * @param taskId The unique identifier of the care task.
     * @param dueAt The epoch timestamp (in milliseconds) when the reminder notification should trigger.
     */
    fun schedule(
        taskId: Long,
        dueAt: Long,
    )

    /**
     * Cancels any pending scheduled reminder notification associated with the given task ID.
     *
     * @param taskId The unique identifier of the care task whose reminder should be canceled.
     */
    fun cancel(taskId: Long)
}
