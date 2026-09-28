package com.traffipart.polanty.data.remeinder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.traffipart.polanty.R
import com.traffipart.polanty.domain.model.CareTask
import com.traffipart.polanty.domain.model.displayName
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CareNotificationManager
    @Inject
    constructor(
        @ApplicationContext
        private val context: Context,
    ) {
        @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
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

        private fun createChannel() {
            val manager = context.getSystemService(NotificationManager::class.java,)

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "Plant care reminders",
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
            manager.createNotificationChannel(channel,)
        }

        private companion object {
            const val CHANNEL_ID =
                "plant_care_reminders"
        }
    }
