package com.traffipart.polanty.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.traffipart.polanty.presentation.root.PlantyRoot
import com.traffipart.polanty.presentation.theme.PolantyTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity for the application.
 *
 * Serves as the single-activity host for Jetpack Compose UI and handles incoming notification intents
 * to navigate directly to specific plant details when a care reminder notification is tapped.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var notificationPlantId by mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        notificationPlantId = intent.notificationPlantId()
        setContent {
            PolantyTheme {
                PlantyRoot(
                    notificationPlantId = notificationPlantId,
                    onNotificationPlantHandled = {
                        notificationPlantId = null
                        intent.removeExtra(EXTRA_NOTIFICATION_PLANT_ID)
                    },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        notificationPlantId = intent.notificationPlantId()
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
        private const val INVALID_PLANT_ID = -1L
    }
}