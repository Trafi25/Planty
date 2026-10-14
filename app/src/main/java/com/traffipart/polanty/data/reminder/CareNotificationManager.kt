package com.traffipart.polanty.data.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.traffipart.polanty.R
import com.traffipart.polanty.domain.model.care.CareTask
import com.traffipart.polanty.domain.model.care.displayName
import com.traffipart.polanty.presentation.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton manager responsible for creating notification channels and displaying plant care notifications.
 *
 * Checks for runtime notification permissions ([Manifest.permission.POST_NOTIFICATIONS]) on Android 13+
 * prior to building and displaying notifications.
 *
 * @property context Application context used for notification system services.
 */
@Singleton
class CareNotificationManager
    @Inject
    constructor(
        @ApplicationContext
        private val context: Context,
    ) {
        /**
         * Displays one expandable notification containing all due care reminders.
         *
         * @param reminders Due tasks paired with their plant display names.
         */
        fun showCareReminder(reminders: List<Pair<CareTask, String>>) {
            if (reminders.isEmpty()) return
            val plantIds = reminders.map { it.first.plantId }.distinct()
            val singlePlantId = plantIds.singleOrNull()
            val contentIntent = createContentIntent(singlePlantId)
            val lines = reminders.map { (task, plantName) -> "$plantName: ${task.type.displayName()}" }
            val title = if (singlePlantId != null) reminders.first().second else "${plantIds.size} plants need care"
            createChannel()

            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            // Remove per-task reminders left visible by older app versions.
            val systemManager = context.getSystemService(NotificationManager::class.java)
            systemManager.activeNotifications
                .filter { it.notification.channelId == CHANNEL_ID && it.tag != NOTIFICATION_TAG }
                .forEach { systemManager.cancel(it.tag, it.id) }

            val inboxStyle = NotificationCompat.InboxStyle()
            lines.forEach { inboxStyle.addLine(it) }
            val notification =
                NotificationCompat
                    .Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(lines.joinToString(" • "))
                    .setStyle(inboxStyle)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .build()

            NotificationManagerCompat
                .from(context)
                .notify(
                    NOTIFICATION_TAG,
                    NOTIFICATION_ID,
                    notification,
                )
        }

        /**
         * Opens plant details for a single plant, or Home for a reminder covering several plants.
         *
         * @param plantId Single plant to open, or null to open Home.
         * @return The configured [PendingIntent].
         */
        private fun createContentIntent(plantId: Long?): PendingIntent {
            val intent =
                Intent(context, MainActivity::class.java).apply {
                    if (plantId != null) {
                        putExtra(MainActivity.EXTRA_NOTIFICATION_PLANT_ID, plantId)
                    } else {
                        putExtra(MainActivity.EXTRA_NOTIFICATION_OPEN_HOME, true)
                    }
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
            return PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        /**
         * Ensures that the dedicated notification channel for plant care reminders exists.
         */
        private fun createChannel() {
            val manager = context.getSystemService(NotificationManager::class.java)

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "Plant care reminders",
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
            manager.createNotificationChannel(channel)
        }

        private companion object {
            const val NOTIFICATION_TAG = "care-summary"
            const val NOTIFICATION_ID = 1
            const val CHANNEL_ID = "plant_care_reminders"
        }
    }
