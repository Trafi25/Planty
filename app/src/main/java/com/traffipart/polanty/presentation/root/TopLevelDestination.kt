package com.traffipart.polanty.presentation.root

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Spa
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents a top-level navigation destination in the app that appears in the bottom bar.
 *
 * @property route The navigation route associated with the destination.
 * @property label The user-facing name of the destination.
 * @property icon The graphical representation of the destination.
 */
internal enum class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    Home(
        route = PlantRoute.HOME,
        label = "Home",
        icon = Icons.Default.Home,
    ),
    Garden(
        route = PlantRoute.GARDEN,
        label = "Garden",
        icon = Icons.Default.Spa,
    ),
    Scan(
        route = PlantRoute.IDENTIFY,
        label = "Scan",
        icon = Icons.Default.CenterFocusStrong,
    ),
    Progress(
        route = PlantRoute.PROGRESS,
        label = "Progress",
        icon = Icons.Default.EmojiEvents,
    ),
}
