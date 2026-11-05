package com.traffipart.polanty.data.reminder

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.service.notification.StatusBarNotification
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class CareNotificationManagerTest {
    private val context = mockk<Context>()
    private val systemManager = mockk<NotificationManager>(relaxed = true)

    init {
        every { context.getSystemService(NotificationManager::class.java) } returns systemManager
    }

    @Test
    fun openingAppClearsCareRemindersAndPreservesOtherNotifications() {
        val care = notification("care-summary", 1, "plant_care_reminders")
        val unrelated = notification("other", 2, "other_channel")
        every { systemManager.activeNotifications } returns arrayOf(care, unrelated)

        CareNotificationManager(context).dismissReminder()

        verify(exactly = 1) { systemManager.cancel("care-summary", 1) }
        verify(exactly = 0) { systemManager.cancel("other", 2) }
    }

    @Test
    fun noDueTasksDoesNotPostNotification() {
        CareNotificationManager(context).showCareReminder(emptyList())

        verify(exactly = 0) { context.getSystemService(NotificationManager::class.java) }
    }

    private fun notification(
        tag: String,
        id: Int,
        channel: String,
    ): StatusBarNotification {
        val content = mockk<Notification>()
        val status = mockk<StatusBarNotification>()
        every { content.channelId } returns channel
        every { status.notification } returns content
        every { status.tag } returns tag
        every { status.id } returns id
        return status
    }
}
