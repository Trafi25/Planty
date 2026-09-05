package com.traffipart.polanty.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.traffipart.polanty.ui.theme.spacing

/**
 * The landing screen of the app, providing a summary of the garden and quick actions.
 *
 * @param onOpenGarden Callback to navigate to the Garden screen.
 * @param onScanPlant Callback to navigate to the Plant Identification flow.
 * @param viewModel The ViewModel providing the home dashboard state.
 */
@Composable
fun HomeScreen(
    onOpenGarden: () -> Unit,
    onScanPlant: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.large),
        verticalArrangement =
            Arrangement.spacedBy(
                MaterialTheme.spacing.medium,
            ),
    ) {
        Text(
            text = "Planty",
            style = MaterialTheme.typography.headlineMedium,
        )
        if (state.isLoading) {
            CircularProgressIndicator()
            return@Column
        }
        Text(
            text =
                "${state.plantCount} plants · " +
                    "${state.spaceCount} spaces",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "Today's care",
            style =
                MaterialTheme.typography
                    .titleLarge,
        )

        Text(
            text =
                "Your daily care tasks will appear here.",
            style =
                MaterialTheme.typography
                    .bodyMedium,
        )
        Button(modifier = Modifier.fillMaxWidth(), onClick = onScanPlant) { Text("Scan a plant") }
        Button(modifier = Modifier.fillMaxWidth(), onClick = onOpenGarden) { Text("Open garden") }
    }
}
