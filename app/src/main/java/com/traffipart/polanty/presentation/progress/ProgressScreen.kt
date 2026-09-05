package com.traffipart.polanty.presentation.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.traffipart.polanty.ui.theme.spacing

/**
 * Screen that displays the user's progress, achievements, and statistics.
 */
@Composable
fun ProgressScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.large),
        verticalArrangement =
            Arrangement.spacedBy(
                MaterialTheme.spacing.medium,
            ),
    ) {
        Text(
            text = "Progress",
            style =
                MaterialTheme.typography
                    .headlineMedium,
        )
        Text(
            text =
                "XP, streaks, levels and achievements will appear here.",
            style =
                MaterialTheme.typography
                    .bodyMedium,
        )
    }
}
