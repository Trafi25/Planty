package com.traffipart.polanty.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.traffipart.polanty.domain.model.care.CareTaskType
import com.traffipart.polanty.domain.model.care.displayName
import com.traffipart.polanty.presentation.theme.spacing

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
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val needNotificationPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    var notificationPermissionGranted by remember {
        mutableStateOf(
            !needNotificationPermission ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED,
        )
    }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            notificationPermissionGranted = isGranted
        }
    LaunchedEffect(
        notificationPermissionGranted,
    ) {
        if (notificationPermissionGranted) {
            viewModel.onAction(
                HomeAction.NotificationPermissionGranted,
            )
        }
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.large),
        verticalArrangement =
            Arrangement.spacedBy(
                MaterialTheme.spacing.medium,
            ),
    ) {
        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically,
        ) {
            Text(
                text = "Planty",
                style =
                    MaterialTheme.typography
                        .headlineMedium,
            )

            IconButton(
                onClick = onOpenSettings,
            ) {
                Icon(
                    imageVector =
                        Icons.Default.Settings,
                    contentDescription =
                        "Settings",
                )
            }
        }
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
                text = "No tasks for today.",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            state.careTasks.forEach { task ->

                HomeCareTaskItem(
                    task = task,
                    onComplete = { viewModel.onAction(HomeAction.CompleteCareTask(task)) },
                    onSoilDry = {
                        viewModel.onAction(
                            HomeAction.SoilCheckResult(
                                task,
                                soilIsDry = true,
                            ),
                        )
                    },
                    onSoilMoist = {
                        viewModel.onAction(
                            HomeAction.SoilCheckResult(
                                task,
                                soilIsDry = false,
                            ),
                        )
                    },
                )
            }
        }

        if (
            needNotificationPermission &&
            !notificationPermissionGranted &&
            state.plantCount > 0
        ) {
            Button(
                onClick = {
                    notificationPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS,
                    )
                },
            ) {
                Text("Enable reminders")
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
 * @param onSoilDry Callback invoked when the user indicates the soil is dry.
 * @param onSoilMoist Callback invoked when the user indicates the soil is moist.
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
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = task.type.displayName(),
                style = MaterialTheme.typography.bodyMedium,
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
