package com.traffipart.polanty.data.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.traffipart.polanty.R
import com.traffipart.polanty.domain.model.care.CareTask
import com.traffipart.polanty.domain.model.care.displayName
import com.traffipart.polanty.domain.reminder.CareReminderRefresher
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import com.traffipart.polanty.domain.repository.plant.PlantRepository
import com.traffipart.polanty.presentation.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Singleton manager responsible for creating notification channels and displaying plant care notifications.
 *
 * Checks for runtime notification permissions ([Manifest.permission.POST_NOTIFICATIONS]) on Android 13+
 * prior to building and displaying notifications.
 *
 * @property context Application context used for notification system services.
 * @property careTaskRepository Supplies current open care tasks when rebuilding the reminder.
 * @property plantRepository Supplies plant display names for due care tasks.
 */
@Singleton
class CareNotificationManager
    @Inject
    constructor(
        @ApplicationContext
        private val context: Context,
        private val careTaskRepository: CareTaskRepository,
        private val plantRepository: PlantRepository,
    ) : CareReminderRefresher {
        private val refreshMutex = Mutex()

        /** Refreshes an existing summary without letting notification failures undo a care action. */
        override suspend fun refreshVisibleReminder() {
            try {
                refreshReminder(onlyIfVisible = true)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w("CareNotificationManager", "Failed to refresh visible care reminder", e)
            }
        }

        /**
         * Reads due tasks and publishes their current summary. Calls are serialized so a worker
         * cannot overwrite a completion refresh with a snapshot read before that completion.
         *
         * @param onlyIfVisible Skip posting when the user has already dismissed the reminder.
         */
        suspend fun refreshReminder(onlyIfVisible: Boolean = false) {
            refreshMutex.withLock {
                if (onlyIfVisible && !isReminderVisible()) return@withLock
                val now = System.currentTimeMillis()
                val dueTasks = careTaskRepository.observeOpenTasks().first()
                    .filter { !it.isCompleted && it.dueAt <= now }
                val reminders = if (dueTasks.isEmpty()) {
                    emptyList()
                } else {
                    val plantsById = plantRepository.observePlants().first().associateBy { it.id }
                    dueTasks.mapNotNull { task ->
                        val plant = plantsById[task.plantId] ?: return@mapNotNull null
                        task to plant.displayName
                    }
                }
                // The user may have dismissed the notification while repositories were being read.
                if (onlyIfVisible && !isReminderVisible()) return@withLock
                showCareReminder(reminders)
            }
        }

        /** Returns whether Android currently displays this manager's shared care notification. */
        private fun isReminderVisible(): Boolean =
            context.getSystemService(NotificationManager::class.java).activeNotifications.any {
                it.tag == NOTIFICATION_TAG && it.id == NOTIFICATION_ID
            }

        /**
         * Displays one expandable notification containing all due care reminders.
         *
         * @param reminders Due tasks paired with their plant display names.
         */
        internal fun showCareReminder(reminders: List<Pair<CareTask, String>>) {
            if (reminders.isEmpty()) {
                context.getSystemService(NotificationManager::class.java).cancel(NOTIFICATION_TAG, NOTIFICATION_ID)
                return
            }
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
