package com.traffipart.polanty.data.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.traffipart.polanty.R
import com.traffipart.polanty.domain.model.CareTask
import com.traffipart.polanty.domain.model.displayName
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
         * Displays a notification for a due care task if notification permission is granted.
         *
         * @param task The [CareTask] for which the reminder notification is being shown.
         * @param plantName Display name of the plant needing care.
         */
        fun showCareReminder(
            task: CareTask,
            plantName: String,
        ) {
            createChannel()

            if (
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            val notification =
                NotificationCompat
                    .Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(plantName)
                    .setContentText(task.type.displayName())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .build()

            NotificationManagerCompat
                .from(context)
                .notify(
                    task.id.hashCode(),
                    notification,
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
            const val CHANNEL_ID = "plant_care_reminders"
        }
    }
