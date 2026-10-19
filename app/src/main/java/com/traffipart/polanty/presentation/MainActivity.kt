package com.traffipart.polanty.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.traffipart.polanty.data.reminder.CareNotificationManager
import com.traffipart.polanty.presentation.root.PlantyRoot
import com.traffipart.polanty.presentation.theme.PolantyTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main Activity for the application.
 *
 * Serves as the single-activity host for Jetpack Compose UI and handles incoming notification intents
 * to navigate directly to specific plant details when a care reminder notification is tapped.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /** Clears reminders when the user returns to Planty to handle care tasks. */
    @Inject
    lateinit var careNotificationManager: CareNotificationManager

    private var notificationPlantId by mutableStateOf<Long?>(null)
    private var notificationOpenHome by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        notificationPlantId = intent.notificationPlantId()
        notificationOpenHome = intent.getBooleanExtra(EXTRA_NOTIFICATION_OPEN_HOME, false)
        setContent {
            PolantyTheme {
                PlantyRoot(
                    notificationPlantId = notificationPlantId,
                    notificationOpenHome = notificationOpenHome,
                    onNotificationHomeHandled = {
                        notificationOpenHome = false
                        intent.removeExtra(EXTRA_NOTIFICATION_OPEN_HOME)
                    },
                    onNotificationPlantHandled = {
                        notificationPlantId = null
                        intent.removeExtra(EXTRA_NOTIFICATION_PLANT_ID)
                    },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        careNotificationManager.dismissReminder()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        notificationPlantId = intent.notificationPlantId()
        notificationOpenHome = intent.getBooleanExtra(EXTRA_NOTIFICATION_OPEN_HOME, false)
    }

    /**
     * Helper extension to extract the plant ID extra from a notification intent.
     *
     * @return The valid plant ID, or `null` if the intent does not contain a valid plant ID.
     */
    private fun Intent.notificationPlantId(): Long? {
        val plantId =
            this.getLongExtra(
                EXTRA_NOTIFICATION_PLANT_ID,
                INVALID_PLANT_ID,
            )

        return plantId.takeIf { it > 0 }
    }

    companion object {
        /** Intent extra key for passing the target plant ID from care reminder notifications. */
        const val EXTRA_NOTIFICATION_PLANT_ID = "notification_plant_id"

        /** Intent extra directing a reminder for multiple plants to the Home care task list. */
        const val EXTRA_NOTIFICATION_OPEN_HOME = "notification_open_home"
        private const val INVALID_PLANT_ID = -1L
    }
}
