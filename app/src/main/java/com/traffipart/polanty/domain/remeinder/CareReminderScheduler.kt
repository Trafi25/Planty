package com.traffipart.polanty.domain.remeinder

interface CareReminderScheduler {
    fun schedule(
        taskId: Long,
        dueAt: Long,
    )

    fun cancel(taskId: Long)
}
