package com.traffipart.polanty.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.traffipart.polanty.domain.model.CareTaskType
import com.traffipart.polanty.domain.model.displayName
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

        if (state.careTasks.isEmpty()) {
            Text(
                text =
                    "No tasks for today.",
                style =
                    MaterialTheme.typography
                        .bodyMedium,
            )
        } else {
            state.careTasks.forEach { task ->
                HomeCareTaskItem(
                    task = task,
                    onComplete = { viewModel.onAction(HomeAction.CompleteCareTask(task)) },
                    onSoilDry = { viewModel.onAction(HomeAction.SoilCheckResult(task, true)) },
                    onSoilMoist = { viewModel.onAction(HomeAction.SoilCheckResult(task, false)) },
                )
            }
        }

        Button(modifier = Modifier.fillMaxWidth(), onClick = onScanPlant) { Text("Scan a plant") }
        Button(modifier = Modifier.fillMaxWidth(), onClick = onOpenGarden) { Text("Open garden") }
    }
}

/**
 * Renders an individual care task item card on the Home screen with a completion button.
 *
 * @param task The [HomeCareTaskUiModel] representing the care task.
 * @param onComplete Callback invoked when the task completion button is tapped.
 */
@Composable
private fun HomeCareTaskItem(
    task: HomeCareTaskUiModel,
    onComplete: () -> Unit,
    onSoilDry: () -> Unit,
    onSoilMoist: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onSoilDry,
    ) {
        Column(
            modifier =
                Modifier.padding(
                    MaterialTheme.spacing.medium,
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    MaterialTheme.spacing.small,
                ),
        ) {
            Text(
                text = task.plantName,
                style =
                    MaterialTheme.typography.titleMedium,
            )
            Text(
                text = task.type.displayName(),
                style =
                    MaterialTheme.typography.bodyMedium,
            )
            when (task.type) {
                CareTaskType.CheckSoil -> {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onSoilDry,
                    ) {
                        Text("Soil is dry")
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onSoilMoist,
                    ) {
                        Text("Soil is still moist")
                    }
                }

                CareTaskType.Water -> {
                    Button(modifier = Modifier.fillMaxWidth(), onClick = onComplete) {
                        Text("Watered")
                    }
                }
                else -> {
                    Button(modifier = Modifier.fillMaxWidth(), onClick = onComplete) {
                        Text("Done")
                    }
                }
            }
        }
    }
}
