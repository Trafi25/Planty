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

/** Displays one care reminder that opens Home, where the user handles all due tasks. */
@Singleton
class CareNotificationManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        /**
         * Posts one reminder with an expandable list of due tasks. A single task shows its plant
         * name and care action. Tapping always opens Home; tasks are completed inside the app.
         *
         * @param reminders Currently due tasks paired with plant display names.
         */
        fun showCareReminder(reminders: List<Pair<CareTask, String>>) {
            if (reminders.isEmpty()) return
            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            val systemManager = context.getSystemService(NotificationManager::class.java)
            systemManager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Plant care reminders", NotificationManager.IMPORTANCE_DEFAULT),
            )
            // Remove individual reminders posted by earlier app versions.
            systemManager.activeNotifications
                .filter { it.notification.channelId == CHANNEL_ID && it.tag != NOTIFICATION_TAG }
                .forEach { systemManager.cancel(it.tag, it.id) }

            val single = reminders.singleOrNull()
            val title = if (single != null) "${single.second} needs care" else "${reminders.size} care tasks due"
            val style = NotificationCompat.InboxStyle()
            reminders.forEach { (task, name) -> style.addLine("$name: ${task.type.displayName()}") }
            val intent =
                Intent(context, MainActivity::class.java).apply {
                    putExtra(MainActivity.EXTRA_NOTIFICATION_OPEN_HOME, true)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
            val contentIntent =
                PendingIntent.getActivity(
                    context,
                    NOTIFICATION_ID,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
            val notification =
                NotificationCompat
                    .Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(single?.first?.type?.displayName() ?: "Open Planty to care for your plants")
                    .setStyle(style)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .build()
            NotificationManagerCompat.from(context).notify(NOTIFICATION_TAG, NOTIFICATION_ID, notification)
        }

        /** Clears care reminders when the app opens and the user can handle tasks inside Planty. */
        fun dismissReminder() {
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.activeNotifications
                .filter { it.notification.channelId == CHANNEL_ID }
                .forEach { manager.cancel(it.tag, it.id) }
        }

        private companion object {
            const val NOTIFICATION_TAG = "care-summary"
            const val NOTIFICATION_ID = 1
            const val CHANNEL_ID = "plant_care_reminders"
        }
    }
