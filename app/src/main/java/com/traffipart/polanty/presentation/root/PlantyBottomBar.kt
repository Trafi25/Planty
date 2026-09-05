package com.traffipart.polanty.presentation.root

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * The standard bottom navigation bar for the application.
 *
 * @param currentRoute The route of the currently active destination.
 * @param onDestinationSelected Callback triggered when a new top-level destination is chosen.
 */
@Composable
internal fun PlantyBottomBar(
    currentRoute: String?,
    onDestinationSelected: (TopLevelDestination) -> Unit,
) {
    NavigationBar {
        TopLevelDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label,
                    )
                },
                label = { Text(destination.label) },
            )
        }
    }
}
