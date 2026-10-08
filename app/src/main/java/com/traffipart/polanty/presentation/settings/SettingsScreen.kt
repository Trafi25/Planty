package com.traffipart.polanty.presentation.settings

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.traffipart.polanty.presentation.theme.PolantyTheme
import com.traffipart.polanty.presentation.theme.spacing

/**
 * Stateful wrapper for the Settings screen.
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { granted ->
            if (granted) {
                viewModel.onAction(SettingsAction.SaveCurrentPlantHomeLocation)
            }
        }

    SettingsContent(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
        onRequestLocationPermission = {
            val hasPermission =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                viewModel.onAction(SettingsAction.SaveCurrentPlantHomeLocation)
            } else {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        },
    )
}

/**
 * Stateless Settings content composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    state: SettingsUiState,
    onAction: (SettingsAction) -> Unit,
    onBack: () -> Unit,
    onRequestLocationPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(MaterialTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            Text(
                text = "Plant care",
                style = MaterialTheme.typography.titleLarge,
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(MaterialTheme.spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                ) {
                    Text(
                        text = "Plant home location",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Text(
                        text =
                            if (state.hasPlantHomeLocation) {
                                "Location configured. Planty uses local climate data to improve care recommendations."
                            } else {
                                "Set the location where your plants live so Planty can adapt care to local climate."
                            },
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isSavingLocation,
                        onClick = onRequestLocationPermission,
                    ) {
                        Text(
                            when {
                                state.isSavingLocation -> "Saving..."
                                state.hasPlantHomeLocation -> "Update location"
                                else -> "Use current location"
                            },
                        )
                    }

                    if (state.hasPlantHomeLocation) {
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onAction(SettingsAction.ClearPlantHomeLocation)
                            },
                        ) {
                            Text("Remove saved location")
                        }
                    }

                    state.locationErrorMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    PolantyTheme {
        SettingsContent(
            state = SettingsUiState(hasPlantHomeLocation = true, isSavingLocation = false),
            onAction = {},
            onBack = {},
            onRequestLocationPermission = {},
        )
    }
}
